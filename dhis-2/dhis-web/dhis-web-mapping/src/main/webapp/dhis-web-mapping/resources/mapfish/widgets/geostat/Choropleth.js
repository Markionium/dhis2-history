/*
 * Copyright (C) 2007-2008  Camptocamp
 *
 * This file is part of MapFish Client
 *
 * MapFish Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MapFish Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MapFish Client.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @requires core/GeoStat/Choropleth.js
 * @requires core/Color.js
 */

Ext.namespace('mapfish.widgets', 'mapfish.widgets.geostat');

mapfish.widgets.geostat.Choropleth = Ext.extend(Ext.FormPanel, {

    /**
     * APIProperty: layer
     * {<OpenLayers.Layer.Vector>} The vector layer containing the features that
     *      are styled based on statistical values. If none is provided, one will
     *      be created.
     */
    layer: null,

    /**
     * APIProperty: format
     * {<OpenLayers.Format>} The OpenLayers format used to get features from
     *      the HTTP request response. GeoJSON is used if none is provided.
     */
    format: null,

    /**
     * APIProperty: url
     * {String} The URL to the web service. If none is provided, the features
     *      found in the provided vector layer will be used.
     */
    url: null,

    /**
     * APIProperty: featureSelection
     * {Boolean} A boolean value specifying whether feature selection must
     *      be put in place. If true a popup will be displayed when the
     *      mouse goes over a feature.
     */
    featureSelection: true,

    /**
     * APIProperty: nameAttribute
     * {String} The feature attribute that will be used as the popup title.
     *      Only applies if featureSelection is true.
     */
    nameAttribute: null,

    /**
     * APIProperty: indicator
     * {String} (read-only) The feature attribute currently chosen
     *     Useful if callbacks are registered on 'featureselected'
     *     and 'featureunselected' events
     */
    indicator: null,

    /**
     * APIProperty: indicatorText
     * {String} (read-only) The raw value of the currently chosen indicator
     *     (ie. human readable)
     *     Useful if callbacks are registered on 'featureselected'
     *     and 'featureunselected' events
     */
    indicatorText: null,

    /**
     * Property: coreComp
     * {<mapfish.GeoStat.ProportionalSymbol>} The core component object.
     */
    coreComp: null,

    /**
     * Property: classificationApplied
     * {Boolean} true if the classify was applied
     */
    classificationApplied: false,

    /**
     * Property: ready
     * {Boolean} true if the widget is ready to accept user commands.
     */
    ready: false,

    /**
     * Property: border
     *     Styling border
     */
    border: false,

    /**
     * APIProperty: loadMask
     *     An Ext.LoadMask config or true to mask the widget while loading (defaults to false).
     */
    loadMask: false,

    /**
     * APIProperty: labelGenerator
     *     Generator for bin labels
     */
    labelGenerator: null,

    /**
     * Constructor: mapfish.widgets.geostat.Choropleth
     *
     * Parameters:
     * config - {Object} Config object.
     */

    /**
     * Method: initComponent
     *    Inits the component
     */
	 
	colorInterpolation: false,
	
	imageLegend: false,
	
	bounds: false,
    
    parentId: false,
    
    newUrl: false,
	
	applyPredefinedLegend: function() {
		var mls = Ext.getCmp('maplegendset_cb').getValue();
		var bounds = [];
		Ext.Ajax.request({
			url: path_mapping + 'getMapLegendsByMapLegendSet' + type,
			method: 'POST',
			params: { mapLegendSetId: mls },
			success: function(r) {
				var mapLegends = Ext.util.JSON.decode(r.responseText).mapLegends;
				var colors = [];
				var bounds = [];
				for (var i = 0; i < mapLegends.length; i++) {
					if (bounds[bounds.length-1] != mapLegends[i].startValue) {
						if (bounds.length !== 0) {
							colors.push(new mapfish.ColorRgb(240,240,240));
						}
						bounds.push(mapLegends[i].startValue);
					}
					colors.push(new mapfish.ColorRgb());
					colors[colors.length-1].setFromHex(mapLegends[i].color);
					bounds.push(mapLegends[i].endValue);
				}

				choropleth.colorInterpolation = colors;
				choropleth.bounds = bounds;
				choropleth.classify(false);
			},
			failure: function() {
				alert('Error: getMapLegendsByMapLegendSet');
			}
		});
	},
	
    initComponent : function() {
    
        mapViewStore = new Ext.data.JsonStore({
            url: path_mapping + 'getAllMapViews' + type,
            root: 'mapViews',
            fields: ['id', 'name'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: true,
            listeners: {
                'load': {
                    fn: function() {
                        if (PARAMETER) {
                            Ext.Ajax.request({
                                url: path_mapping + 'getMapView' + type,
                                method: 'POST',
                                params: { id: PARAMETER },
								success: function(r) {
									PARAMETER = false;
                                    MAPVIEW = Ext.util.JSON.decode(r.responseText).mapView[0];
                                    MAPSOURCE = MAPVIEW.mapSourceType;
									Ext.getCmp('mapsource_cb').setValue(MAPSOURCE);
                                    Ext.getCmp('mapview_cb').setValue(MAPVIEW.id);
									
									if (MAPVIEW.mapLegendType == map_legend_type_automatic) {
										Ext.getCmp('maplegendtype_cb').setValue(map_legend_type_automatic);
										Ext.getCmp('numClasses_cb').setValue(MAPVIEW.classes);
										Ext.getCmp('colorA_cf').setValue(MAPVIEW.colorLow);
										Ext.getCmp('colorB_cf').setValue(MAPVIEW.colorHigh);
										
										Ext.getCmp('method_cb').showField();
										Ext.getCmp('bounds_tf').hideField();
										Ext.getCmp('numClasses_cb').showField();
										Ext.getCmp('colorA_cf').showField();
										Ext.getCmp('colorB_cf').showField();
										Ext.getCmp('maplegendset_cb').hideField();
									}
									else if (MAPVIEW.mapLegendType == map_legend_type_predefined) {
                                        LEGEND[thematicMap].type = map_legend_type_predefined;
										Ext.getCmp('maplegendtype_cb').setValue(map_legend_type_predefined);
										Ext.getCmp('method_cb').hideField();
										Ext.getCmp('bounds_tf').hideField();
										Ext.getCmp('numClasses_cb').hideField();
										Ext.getCmp('colorA_cf').hideField();
										Ext.getCmp('colorB_cf').hideField();
										Ext.getCmp('maplegendset_cb').showField();
										
										predefinedMapLegendSetStore.load();
									}										
										
									MAP.setCenter(new OpenLayers.LonLat(MAPVIEW.longitude, MAPVIEW.latitude), MAPVIEW.zoom);

                                    Ext.getCmp('indicatorgroup_cb').setValue(MAPVIEW.indicatorGroupId);
                                    
                                    var igId = MAPVIEW.indicatorGroupId;
                                    indicatorStore.baseParams = { indicatorGroupId: igId, format: 'json' };
                                    indicatorStore.reload();
                                },
                                failure: function() {
                                  alert( i18n_status , i18n_error_while_retrieving_data );
                                }
                            });
                        }
                    },
                    scope: this
                }
            }
        });
    
        indicatorGroupStore = new Ext.data.JsonStore({
            url: path_mapping + 'getAllIndicatorGroups' + type,
            root: 'indicatorGroups',
            fields: ['id', 'name'],
            idProperty: 'id',
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: true
        });
        
        indicatorStore = new Ext.data.JsonStore({
            url: path_mapping + 'getIndicatorsByIndicatorGroup' + type,
			baseParams: {indicatorGroupId:0},
            root: 'indicators',
            fields: ['id', 'name', 'shortName'],
            idProperty: 'id',
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: false,
            listeners: {
                'load': {
                    fn: function() {
                        indicatorStore.each(
                            function fn(record) {
                                var name = record.get('name');
                                name = name.replace('&lt;', '<').replace('&gt;', '>');
                                record.set('name', name);
                            },  this
                        );
                        
                        Ext.getCmp('indicator_cb').clearValue();

                        if (MAPVIEW) {
                            Ext.getCmp('indicator_cb').setValue(MAPVIEW.indicatorId);
                            Ext.getCmp('periodtype_cb').setValue(MAPVIEW.periodTypeId);
                            periodStore.baseParams = {name: MAPVIEW.periodTypeId};
                            periodStore.reload();
                        }
                    }
                }
            }
        });
		
		dataElementGroupStore = new Ext.data.JsonStore({
			url: path_mapping + 'getAllDataElementGroups' + type,
            root: 'dataElementGroups',
            fields: ['id', 'name'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: true
        });
		
		dataElementStore = new Ext.data.JsonStore({
            url: path_mapping + 'getDataElementsByDataElementGroup' + type,
            root: 'dataElements',
            fields: ['id', 'name', 'shortName'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: false,
            listeners: {
                'load': {
                    fn: function() {
                        dataElementStore.each(
                        function fn(record) {
                                var name = record.get('name');
                                name = name.replace('&lt;', '<').replace('&gt;', '>');
                                record.set('name', name);
                            },  this
                        );
                        
                        Ext.getCmp('dataelement_cb').clearValue();

                        if (MAPVIEW) {
                            Ext.getCmp('dataelement_cb').setValue(MAPVIEW.dataElementId);
                            Ext.getCmp('periodtype_cb').setValue(MAPVIEW.periodTypeId);
                            periodStore.baseParams = {name: MAPVIEW.periodTypeId};
                            periodStore.reload();
                        }
                    },
                    scope: this
                }
            }
        });
        
        periodTypeStore = new Ext.data.JsonStore({
            url: path_mapping + 'getAllPeriodTypes' + type,
            root: 'periodTypes',
            fields: ['name'],
            autoLoad: true
        });
            
        periodStore = new Ext.data.JsonStore({
            url: path_mapping + 'getPeriodsByPeriodType' + type,
            baseParams: { name: 0 },
            root: 'periods',
            fields: ['id', 'name'],
            autoLoad: false,
            listeners: {
                'load': {
                    fn: function() {
                        if (MAPVIEW) {
                            Ext.getCmp('period_cb').setValue(MAPVIEW.periodId);
                            var mst = MAPVIEW.mapSourceType;

                            Ext.Ajax.request({
                                url: path_mapping + 'setMapSourceTypeUserSetting' + type,
                                method: 'POST',
                                params: { mapSourceType: mst },
								success: function(r) {
                                    Ext.getCmp('map_cb').getStore().reload();
                                    Ext.getCmp('maps_cb').getStore().reload();
                                    
                                    Ext.getCmp('mapsource_cb').setValue(MAPSOURCE);
                                },
                                failure: function() {
                                    alert( 'Error: setMapSourceTypeUserSetting' );
                                }
                            });
                            
                            this.newUrl = MAPVIEW.mapSource;
                        }
                    },
                    scope: this
                }
            }
        });
            
        mapStore = new Ext.data.JsonStore({
            url: path_mapping + 'getAllMaps' + type,
            baseParams: { format: 'jsonmin' },
            root: 'maps',
            fields: ['id', 'name', 'mapLayerPath', 'organisationUnitLevel'],
            idProperty: 'mapLayerPath',
            autoLoad: true,
            listeners: {
                'load': {
                    fn: function() {
                        if (MAPVIEW) {
                            Ext.getCmp('map_cb').setValue(MAPVIEW.mapSource);
                            choropleth.classify(false, true);
                        }
                    }
                }
            }
        });
		
		predefinedMapLegendSetStore = new Ext.data.JsonStore({
            url: path_mapping + 'getMapLegendSetsByType' + type,
            baseParams: { type: map_legend_type_predefined },
            root: 'mapLegendSets',
            fields: ['id', 'name'],
            autoLoad: true,
            listeners: {
                'load': {
                    fn: function() {
						if (MAPVIEW) {
							Ext.Ajax.request({
								url: path_mapping + 'getMapLegendSet' + type,
								method: 'POST',
								params: { id: MAPVIEW.mapLegendSetId },
								success: function(r) {
									var mls = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
									Ext.getCmp('maplegendset_cb').setValue(mls.id);
									choropleth.applyPredefinedLegend();
								},
								failure: function() {
									alert('Error: getMapLegendSet');
								}
							});
						}
                    }
                }
            }
        });
        
        this.items = [
         
        {
            xtype: 'combo',
            id: 'mapview_cb',
            fieldLabel: i18n_favorite,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: i18n_optional,
            selectOnFocus: true,
			labelSeparator: labelseparator,
            width: combo_width,
            store: mapViewStore,
            listeners: {
                'select': {
                    fn: function() {
                        var mId = Ext.getCmp('mapview_cb').getValue();
                        
                        Ext.Ajax.request({
                            url: path_mapping + 'getMapView' + type,
                            method: 'POST',
                            params: { id: mId },
                            success: function(r) {
                                MAPVIEW = Ext.util.JSON.decode(r.responseText).mapView[0];
								MAPSOURCE = MAPVIEW.mapSourceType;
                                
                                Ext.getCmp('mapvaluetype_cb').setValue(MAPVIEW.mapValueType);
								VALUETYPE.polygon = MAPVIEW.mapValueType;
                                
                                if (MAPVIEW.mapValueType == map_value_type_indicator) {
                                    Ext.getCmp('indicatorgroup_cb').showField();
                                    Ext.getCmp('indicator_cb').showField();
                                    Ext.getCmp('dataelementgroup_cb').hideField();
                                    Ext.getCmp('dataelement_cb').hideField();
                                    
                                    Ext.getCmp('indicatorgroup_cb').setValue(MAPVIEW.indicatorGroupId);
                                    indicatorStore.baseParams = { indicatorGroupId: MAPVIEW.indicatorGroupId };
                                    indicatorStore.reload();
                                }
                                else if (MAPVIEW.mapValueType == map_value_type_dataelement) {
                                    Ext.getCmp('indicatorgroup_cb').hideField();
                                    Ext.getCmp('indicator_cb').hideField();
                                    Ext.getCmp('dataelementgroup_cb').showField();
                                    Ext.getCmp('dataelement_cb').showField();
                                    
                                    Ext.getCmp('dataelementgroup_cb').setValue(MAPVIEW.dataElementGroupId);
                                    dataElementStore.baseParams = { dataElementGroupId: MAPVIEW.dataElementGroupId };
                                    dataElementStore.reload();
                                }                                        
								
								if (MAPVIEW.mapLegendType == map_legend_type_automatic) {
									Ext.getCmp('maplegendtype_cb').setValue(map_legend_type_automatic);
									Ext.getCmp('numClasses_cb').setValue(MAPVIEW.classes);
									Ext.getCmp('colorA_cf').setValue(MAPVIEW.colorLow);
									Ext.getCmp('colorB_cf').setValue(MAPVIEW.colorHigh);
									
									Ext.getCmp('method_cb').showField();
									Ext.getCmp('bounds_tf').hideField();
									Ext.getCmp('numClasses_cb').showField();
									Ext.getCmp('colorA_cf').showField();
									Ext.getCmp('colorB_cf').showField();
									Ext.getCmp('maplegendset_cb').hideField();
								}
								else if (MAPVIEW.mapLegendType == map_legend_type_predefined) {
                                    LEGEND[thematicMap].type = map_legend_type_predefined;
									Ext.getCmp('maplegendtype_cb').setValue(map_legend_type_predefined);
									Ext.getCmp('method_cb').hideField();
									Ext.getCmp('bounds_tf').hideField();
									Ext.getCmp('numClasses_cb').hideField();
									Ext.getCmp('colorA_cf').hideField();
									Ext.getCmp('colorB_cf').hideField();
									Ext.getCmp('maplegendset_cb').showField();
									
                                    Ext.getCmp('maplegendset_cb').setValue(MAPVIEW.mapLegendSetId);
                                    choropleth.applyPredefinedLegend();
								}
                            },
                            failure: function() {
                              alert( i18n_status , i18n_error_while_retrieving_data );
                            } 
                        });
                    },
                    scope: this
                }
            }
        },
        
        { html: '<br>' },
		
		{
            xtype: 'combo',
			id: 'mapvaluetype_cb',
            fieldLabel: i18n_mapvaluetype,
			labelSeparator: labelseparator,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'local',
            triggerAction: 'all',
            width: combo_width,
			value: map_value_type_indicator,
            store: new Ext.data.SimpleStore({
                fields: ['id', 'name'],
                data: [[map_value_type_indicator, 'Indicators'], [map_value_type_dataelement, 'Data elements']]
            }),
			listeners: {
				'select': {
					fn: function() {
						if (Ext.getCmp('mapvaluetype_cb').getValue() == map_value_type_indicator) {
							Ext.getCmp('indicatorgroup_cb').showField();
							Ext.getCmp('indicator_cb').showField();
							Ext.getCmp('dataelementgroup_cb').hideField();
							Ext.getCmp('dataelement_cb').hideField();
							VALUETYPE.polygon = map_value_type_indicator;
						}
						else if (Ext.getCmp('mapvaluetype_cb').getValue() == map_value_type_dataelement) {
							Ext.getCmp('indicatorgroup_cb').hideField();
							Ext.getCmp('indicator_cb').hideField();
							Ext.getCmp('dataelementgroup_cb').showField();
							Ext.getCmp('dataelement_cb').showField();
							VALUETYPE.polygon = map_value_type_dataelement;
						}
					}
				}
			}
		},
        
        {
            xtype: 'combo',
            id: 'indicatorgroup_cb',
            fieldLabel: i18n_indicator_group,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: indicatorGroupStore,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb').getValue()) {
                            Ext.getCmp('mapview_cb').clearValue();
                        }
						
						Ext.getCmp('indicator_cb').clearValue();
						indicatorStore.setBaseParam('indicatorGroupId', this.getValue());
                        indicatorStore.reload();
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'indicator_cb',
            fieldLabel: i18n_indicator ,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: indicatorStore,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb').getValue()) {
                            Ext.getCmp('mapview_cb').reset();
                        }
 
                        var iId = Ext.getCmp('indicator_cb').getValue();
                        
                        Ext.Ajax.request({
                            url: path_mapping + 'getMapLegendSetByIndicator' + type,
                            method: 'POST',
                            params: { indicatorId: iId, format: 'json' },

                            success: function( responseObject ) {
                                var data = Ext.util.JSON.decode(responseObject.responseText);
                                
                                if (data.mapLegendSet[0].id != '') {
//                                    Ext.getCmp('method_cb').setValue(data.mapLegendSet[0].method);
                                    Ext.getCmp('numClasses_cb').setValue(data.mapLegendSet[0].classes);

                                    Ext.getCmp('colorA_cf').setValue(data.mapLegendSet[0].colorLow);
                                    Ext.getCmp('colorB_cf').setValue(data.mapLegendSet[0].colorHigh);
                                }
                                
                                choropleth.classify(false);
                            },
                            failure: function()
                            {
                              alert( i18n_status , i18n_error_while_retrieving_data );
                            } 
                        });
                    },
                    scope: this
                }
            }
        },
		
		{
            xtype: 'combo',
            id: 'dataelementgroup_cb',
            fieldLabel: i18n_dataelement_group,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: dataElementGroupStore,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb').getValue()) {
                            Ext.getCmp('mapview_cb').reset();
                        }
                        Ext.getCmp('dataelement_cb').reset();
						dataElementStore.setBaseParam('dataElementGroupId', this.getValue());
                        dataElementStore.reload();
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'dataelement_cb',
            fieldLabel: i18n_dataelement ,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: dataElementStore,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb').getValue()) {
                            Ext.getCmp('mapview_cb').reset();
                        }
						
						choropleth.classify(false);
 
                        // var iId = Ext.getCmp('dataelement_cb').getValue();
                        
						/* TODO legend set
						
                        Ext.Ajax.request({
                            url: path_mapping + 'getMapLegendSetByIndicator' + type,
                            method: 'POST',
                            params: { indicatorId: iId },

                            success: function( responseObject ) {
                                var data = Ext.util.JSON.decode(responseObject.responseText);
                                
                                if (data.mapLegendSet[0].id != '') {
                                   // Ext.getCmp('method_cb').setValue(data.mapLegendSet[0].method);
                                    Ext.getCmp('numClasses_cb').setValue(data.mapLegendSet[0].classes);

                                    Ext.getCmp('colorA_cf').setValue(data.mapLegendSet[0].colorLow);
                                    Ext.getCmp('colorB_cf').setValue(data.mapLegendSet[0].colorHigh);
                                }
                                
                                choropleth.classify(false);
                            },
                            failure: function()
                            {
                              alert( i18n_status , i18n_error_while_retrieving_data );
                            } 
                        });
						*/
                    },
                    scope: this
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'periodtype_cb',
            fieldLabel: i18n_period_type,
            typeAhead: true,
            editable: false,
            valueField: 'name',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: periodTypeStore,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb').getValue() != '') {
                            Ext.getCmp('mapview_cb').reset();
                        }
                        
                        var pt = Ext.getCmp('periodtype_cb').getValue();
                        Ext.getCmp('period_cb').getStore().baseParams = { name: pt, format: 'json' };
                        Ext.getCmp('period_cb').getStore().reload();
                    },
                    scope: this
                }
            }
        },

        {
            xtype: 'combo',
            id: 'period_cb',
            fieldLabel: i18n_period ,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: periodStore,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb').getValue() != '') {
                            Ext.getCmp('mapview_cb').reset();
                        }
                        
                        this.classify(false);
                    },
                    scope: this
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'map_cb',
            fieldLabel: i18n_map ,
            typeAhead: true,
            editable: false,
            valueField: 'mapLayerPath',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: mapStore,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb').getValue() != '') {
                            Ext.getCmp('mapview_cb').reset();
                        }
                        
                        choropleth.loadByUrl(Ext.getCmp('map_cb').getValue());
                    },
                    scope: this
                }
            }
        },
        
        {
            xtype: 'textfield',
            id: 'map_tf',
            fieldLabel: i18n_parent_orgunit,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            listeners: {
                'focus': {
                    fn: function() {
                        function showTree() {
                            var value, rawvalue;
                            var w = new Ext.Window({
                                id: 'orgunit_w',
                                title: 'Select parent organisation unit',
                                closeAction: 'hide',
                                autoScroll: true,
                                width: 280,
                                autoHeight: true,
                                height: 'auto',
                                boxMaxHeight: 500,
                                items: [
                                    {
                                        xtype: 'treepanel',
                                        id: 'orgunit_tp',
                                        bodyStyle: 'padding:7px',
                                        loader: new Ext.tree.TreeLoader({
                                            dataUrl: path_mapping + 'getOrganisationUnitChildren' + type
                                        }),
                                        root: {
                                            id: TOPLEVELUNIT.id,
                                            text: TOPLEVELUNIT.name,
                                            nodeType: 'async',
                                            draggable: false,
                                            expanded: true
                                        },
                                        listeners: {
                                            'click': {
                                                fn: function(n) {
                                                    if (n.hasChildNodes()) {
                                                        Ext.getCmp('map_tf').setValue(n.attributes.text);
                                                        Ext.getCmp('map_tf').value = n.attributes.id;
                                                        Ext.getCmp('map_tf').node = n;
                                                    }
                                                }
                                            },
                                            'expandnode': {
                                                fn: function(n) {
                                                    Ext.getCmp('orgunit_w').syncSize();
                                                }
                                            },
                                            'collapsenode': {
                                                fn: function(n) {
                                                    Ext.getCmp('orgunit_w').syncSize();
                                                }
                                            }
                                        }
                                    },
                                    {
                                        xtype: 'panel',
                                        layout: 'fit',
                                        items: [
                                            {
                                                xtype: 'button',
                                                text: 'OK',
                                                handler: function() {
                                                    choropleth.loadById(Ext.getCmp('map_tf').value);
                                                    Ext.getCmp('orgunit_w').hide();
                                                }                                                
                                            }
                                        ]
                                    }
                                ]
                            }).show();
                        }
                        
                        if (TOPLEVELUNIT.id) {
                            showTree();
                        }
                        else {
                            Ext.Ajax.request({
                                url: path_commons + 'getOrganisationUnits' + type,
                                params: { level: 1 },
                                method: 'POST',
                                success: function(r) {
                                    var rootNode = Ext.util.JSON.decode(r.responseText).organisationUnits[0];
                                    TOPLEVELUNIT.id = rootNode.id;
                                    TOPLEVELUNIT.name = rootNode.name;
                                    
                                    showTree();          
                                },
                                failure: function(r) {
                                    alert('getOrganisationUnits');
                                }
                            });
                        }
                    },
                    scope: this
                }
            }
        },
        
        { html: '<br>' },
		
		{
            xtype: 'combo',
            fieldLabel: i18n_legend_type,
            id: 'maplegendtype_cb',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            value: LEGEND[thematicMap].type,
            triggerAction: 'all',
            width: combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value', 'text'],
                data: [
					[map_legend_type_automatic, i18n_automatic],
					[map_legend_type_predefined, i18n_predefined]
				]
            }),
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('maplegendtype_cb').getValue() == map_legend_type_predefined && Ext.getCmp('maplegendtype_cb').getValue() != LEGEND[thematicMap].type ) {
							LEGEND[thematicMap].type = map_legend_type_predefined;
							Ext.getCmp('method_cb').hideField();
							Ext.getCmp('bounds_tf').hideField();
                            Ext.getCmp('numClasses_cb').hideField();
							Ext.getCmp('colorA_cf').hideField();
							Ext.getCmp('colorB_cf').hideField();
							Ext.getCmp('maplegendset_cb').showField();
							
							if (Ext.getCmp('maplegendset_cb').getValue()) {
								this.classify(false);
							}
                        }
                        else if (Ext.getCmp('maplegendtype_cb').getValue() == map_legend_type_automatic && Ext.getCmp('maplegendtype_cb').getValue() != LEGEND[thematicMap].type) {
							LEGEND[thematicMap].type = map_legend_type_automatic;
							Ext.getCmp('method_cb').showField();
							if (Ext.getCmp('method_cb').getValue() == 0) {
								Ext.getCmp('bounds_tf').showField();
								Ext.getCmp('numClasses_cb').hideField();
							}
							else {
								Ext.getCmp('bounds_tf').hideField();
								Ext.getCmp('numClasses_cb').showField();
							}
							Ext.getCmp('colorA_cf').showField();
							Ext.getCmp('colorB_cf').showField();
							Ext.getCmp('maplegendset_cb').hideField();
                            
                            this.classify(false);
                        }
                    },
                    scope: this
                }
            }
        },
		
		{
            xtype: 'combo',
            fieldLabel: i18n_legend_set,
            id: 'maplegendset_cb',
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            triggerAction: 'all',
            width: combo_width,
			hidden: true,
            store: predefinedMapLegendSetStore,
            listeners: {
                'select': {
                    fn: function() {
						choropleth.applyPredefinedLegend();
                    },
                    scope: this
                }
            }
        },

        {
            xtype: 'combo',
            fieldLabel: i18n_method,
            id: 'method_cb',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            value: LEGEND[thematicMap].method,
            triggerAction: 'all',
            width: combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value', 'text'],
                data: [
					[1, i18n_equal_intervals],
					[2, i18n_equal_group_count],
					[0, i18n_fixed_breaks]
				]
            }),
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('method_cb').getValue() == 0 && Ext.getCmp('method_cb').getValue() != LEGEND[thematicMap].method) {
							LEGEND[thematicMap].method = 0;
                            Ext.getCmp('bounds_tf').showField();
                            Ext.getCmp('numClasses_cb').hideField();
                        }
                        else if (Ext.getCmp('method_cb').getValue() != LEGEND[thematicMap].method) {
							LEGEND[thematicMap].method = Ext.getCmp('method_cb').getValue();
                            Ext.getCmp('bounds_tf').hideField();
                            Ext.getCmp('numClasses_cb').showField();
                            
                            this.classify(false);
                        }
                    },
                    scope: this
                }
            }
        },
        
        {
            xtype: 'textfield',
            id: 'bounds_tf',
            fieldLabel: i18n_bounds,
			labelSeparator: labelseparator,
            emptyText: i18n_comma_separated_values,
            isFormField: true,
            width: combo_width,
            hidden: true
        },
        
        {
            xtype: 'combo',
            fieldLabel: i18n_classes ,
			labelSeparator: labelseparator,
            id: 'numClasses_cb',
            editable: false,
            valueField: 'value',
            displayField: 'value',
            mode: 'local',
            value: LEGEND[thematicMap].classes,
            triggerAction: 'all',
            width: combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value'],
                data: [[1], [2], [3], [4], [5], [6], [7]]
            }),
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb').getValue() != '') {
                            Ext.getCmp('mapview_cb').reset();
                        }
						
						if (Ext.getCmp('numClasses_cb').getValue() != LEGEND[thematicMap].classes) {
							LEGEND[thematicMap].classes = Ext.getCmp('numClasses_cb').getValue();
							this.classify(false);
						}
                    },
                    scope: this
                }
            }
        },

        {
            xtype: 'colorfield',
            fieldLabel: i18n_low_color,
			labelSeparator: labelseparator,
            id: 'colorA_cf',
            allowBlank: false,
            isFormField: true,
            width: combo_width,
            value: "#FFFF00"
        },
        
        {
            xtype: 'colorfield',
            fieldLabel: i18n_high_color,
			labelSeparator: labelseparator,
            id: 'colorB_cf',
            allowBlank: false,
            isFormField: true,
            width: combo_width,
            value: "#FF0000"
        },
        
        { html: '<br>' },

        {
            xtype: 'button',
			cls: 'aa_med',
            isFormField: true,
            fieldLabel: '',
            labelSeparator: '',
            text: i18n_refresh,
            handler: function() {
                this.layer.setVisibility(true);
                this.classify(true);
            },
            scope: this
        }

        ];
	
		mapfish.widgets.geostat.Choropleth.superclass.initComponent.apply(this);
    },
    
    setUrl: function(url) {
        this.url = url;
        this.coreComp.setUrl(this.url);
    },

    requestSuccess: function(request) {
        this.ready = true;

        if (this.loadMask && this.rendered) {
            this.loadMask.hide();
        }
    },

    requestFailure: function(request) {
        OpenLayers.Console.error( i18n_ajax_request_failed );
    },
    
    getColors: function() {
        var colorA = new mapfish.ColorRgb();
        colorA.setFromHex(Ext.getCmp('colorA_cf').getValue());
        var colorB = new mapfish.ColorRgb();
        colorB.setFromHex(Ext.getCmp('colorB_cf').getValue());
        return [colorA, colorB];
    },
    
    loadById: function(id) {
        if (id != choropleth.parentId) {
            choropleth.parentId = id;
            var n = Ext.getCmp('map_tf').node;
            
            Ext.Ajax.request({
                url: path_mapping + 'getOrganisationUnitChildren' + type,
                method: 'POST',
                params: {node:n.attributes.id},
                success: function(r) {
                    var childIsLeaf = Ext.util.JSON.decode(r.responseText)[0].leaf;
                    var url = childIsLeaf ? path_mapping + 'getPointShapefile.action?id=' + id : path_mapping + 'getPolygonShapefile.action?id=' + id;
                    choropleth.setUrl(url);
                },
                failure: function() {
                    alert('Error: getOrganisationUnitChildren');
                }
            });
        }
    },
    
    loadByUrl: function(url) {
        if (url != choropleth.newUrl) {
            choropleth.newUrl = url;
            
            if (MAPSOURCE == map_source_type_geojson) {
                choropleth.setUrl(path_mapping + 'getGeoJson.action?name=' + url);
            }
			else if (MAPSOURCE == map_source_type_shapefile) {
				choropleth.setUrl(path_geoserver + wfs + url + output);
			}
        }
    },
    
    displayMapLegendTypeFields: function() {
        if (LEGEND[thematicMap].type == map_legend_type_automatic) {
			Ext.getCmp('maplegendset_cb').hideField();
		}
		else if (LEGEND[thematicMap].type == map_legend_type_predefined) {
			Ext.getCmp('maplegendset_cb').showField();
		}
    },
    
    validateForm: function(exception) {
        if (Ext.getCmp('mapvaluetype_cb').getValue() == map_value_type_indicator) {
            if (!Ext.getCmp('indicator_cb').getValue()) {
                if (exception) {
                    Ext.messageRed.msg(i18n_thematic_map, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        else if (Ext.getCmp('mapvaluetype_cb').getValue() == map_value_type_dataelement) {
            if (!Ext.getCmp('indicator_cb').getValue()) {
                if (exception) {
                    Ext.messageRed.msg(i18n_thematic_map, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        
        var cmp = MAPSOURCE == map_source_type_database ? Ext.getCmp('map_tf') : Ext.getCmp('map_cb');
        
        if (!Ext.getCmp('period_cb').getValue() || !cmp.getValue()) {
            if (exception) {
                Ext.messageRed.msg(i18n_thematic_map, i18n_form_is_not_complete);
            }
            return false;
        }
        
        return true;
    },
    
    getIndicatorOrDataElementId: function() {
        return VALUETYPE.polygon == map_value_type_indicator ?
            Ext.getCmp('indicator_cb').getValue() : Ext.getCmp('dataelement_cb').getValue();
    },
    
    applyValues: function() {
        var options = {};
        choropleth.indicator = options.indicator = 'value';
        options.method = Ext.getCmp('method_cb').getValue();
        options.numClasses = Ext.getCmp('numClasses_cb').getValue();
        options.colors = choropleth.getColors();
        
        choropleth.coreComp.updateOptions(options);
        choropleth.coreComp.applyClassification();
        choropleth.classificationApplied = true;
    
        MASK.hide();
    },

    classify: function(exception, position) {
        if (MAPSOURCE == map_source_type_database) {
            choropleth.classifyDatabase(exception, position);
        }
        else {
            choropleth.classifyFile(exception, position);
        }
    },
    
    classifyDatabase: function(exception, position) {
		choropleth.displayMapLegendTypeFields();
        if (choropleth.validateForm(exception)) {
        
            MASK.msg = i18n_creating_choropleth;
            MASK.show();        
            
            MAPDATA[ACTIVEPANEL].name = Ext.getCmp('map_tf').getValue();
            MAPDATA[ACTIVEPANEL].nameColumn = 'name';
            MAPDATA[ACTIVEPANEL].longitude = BASECOORDINATE.longitude;
            MAPDATA[ACTIVEPANEL].latitude = BASECOORDINATE.latitude;
            MAPDATA[ACTIVEPANEL].zoom = 7;
            
            if (!position) {
                if (MAPDATA[ACTIVEPANEL].zoom != MAP.getZoom()) {
                    MAP.zoomTo(MAPDATA[ACTIVEPANEL].zoom);
                }
                MAP.setCenter(new OpenLayers.LonLat(MAPDATA[ACTIVEPANEL].longitude, MAPDATA[ACTIVEPANEL].latitude));
            }
            
            if (MAPVIEW) {
                if (MAPVIEW.longitude && MAPVIEW.latitude && MAPVIEW.zoom) {
                    MAP.setCenter(new OpenLayers.LonLat(MAPVIEW.longitude, MAPVIEW.latitude), MAPVIEW.zoom);
                }
                else {
                    MAP.setCenter(new OpenLayers.LonLat(MAPDATA[ACTIVEPANEL].longitude, MAPDATA[ACTIVEPANEL].latitude), MAPDATA[ACTIVEPANEL].zoom);
                }
                MAPVIEW = false;
            }
            
            var polygonLayer = MAP.getLayersByName('Polygon layer')[0];
            FEATURE[thematicMap] = polygonLayer.features;
            
            if (LABELS[thematicMap]) {
                toggleFeatureLabelsPolygons(false, polygonLayer);
            }
            
            var indicatorOrDataElementId = VALUETYPE.polygon == map_value_type_indicator ?
                Ext.getCmp('indicator_cb').getValue() : Ext.getCmp('dataelement_cb').getValue();
            var dataUrl = VALUETYPE.polygon == map_value_type_indicator ?
                'getIndicatorMapValuesByParentOrganisationUnit' : 'getDataMapValuesByParentOrganisationUnit';
            var periodId = Ext.getCmp('period_cb').getValue();
            var parentId = choropleth.parentId;
            
            Ext.Ajax.request({
                url: path_mapping + dataUrl + type,
                method: 'POST',
                params: {id:indicatorOrDataElementId, periodId:periodId, parentId:parentId},
                success: function(r) {
                    var mapvalues = Ext.util.JSON.decode(r.responseText).mapvalues;
                    EXPORTVALUES = getExportDataValueJSON(mapvalues);
                    
                    if (mapvalues.length == 0) {
                        Ext.messageRed.msg( i18n_thematic_map, i18n_current_selection_no_data );
                        MASK.hide();
                        return;
                    }
                    
                    for (var i = 0; i < mapvalues.length; i++) {
                        for (var j = 0; j < FEATURE[thematicMap].length; j++) {
                            if (mapvalues[i].orgUnitName == FEATURE[thematicMap][j].attributes.name) {
                                FEATURE[thematicMap][j].attributes.value = parseFloat(mapvalues[i].value);
                                break;
                            }
                        }
                    }
                    
                    choropleth.applyValues();
                },
                failure: function(r) {
                    alert('Error: ' + dataUrl);
                }
            });
        }
    },
    
    classifyFile: function(exception, position) {
		choropleth.displayMapLegendTypeFields();
        if (choropleth.validateForm(exception)) {
        
            MASK.msg = i18n_creating_choropleth;
            MASK.show();
            
            Ext.Ajax.request({
                url: path_mapping + 'getMapByMapLayerPath' + type,
                method: 'POST',
                params: { mapLayerPath: choropleth.newUrl },
                success: function(r) {
                    MAPDATA[ACTIVEPANEL] = Ext.util.JSON.decode(r.responseText).map[0];
                    
                    MAPDATA[ACTIVEPANEL].organisationUnitLevel = parseFloat(MAPDATA[ACTIVEPANEL].organisationUnitLevel);
                    MAPDATA[ACTIVEPANEL].longitude = parseFloat(MAPDATA[ACTIVEPANEL].longitude);
                    MAPDATA[ACTIVEPANEL].latitude = parseFloat(MAPDATA[ACTIVEPANEL].latitude);
                    MAPDATA[ACTIVEPANEL].zoom = parseFloat(MAPDATA[ACTIVEPANEL].zoom);
                    
                    if (!position) {
                        if (MAPDATA[ACTIVEPANEL].zoom != MAP.getZoom()) {
                            MAP.zoomTo(MAPDATA[ACTIVEPANEL].zoom);
                        }
                        MAP.setCenter(new OpenLayers.LonLat(MAPDATA[ACTIVEPANEL].longitude, MAPDATA[ACTIVEPANEL].latitude));
                    }
                    
                    if (MAPVIEW) {
                        if (MAPVIEW.longitude && MAPVIEW.latitude && MAPVIEW.zoom) {
                            MAP.setCenter(new OpenLayers.LonLat(MAPVIEW.longitude, MAPVIEW.latitude), MAPVIEW.zoom);
                        }
                        else {
                            MAP.setCenter(new OpenLayers.LonLat(MAPDATA[ACTIVEPANEL].longitude, MAPDATA[ACTIVEPANEL].latitude), MAPDATA[ACTIVEPANEL].zoom);
                        }
                        MAPVIEW = false;
                    }
            
                    var polygonLayer = MAP.getLayersByName('Polygon layer')[0];
                    FEATURE[thematicMap] = polygonLayer.features;
                    
                    if (LABELS[thematicMap]) {
                        toggleFeatureLabelsPolygons(false, polygonLayer);
                    }
            
                    var indicatorOrDataElementId = VALUETYPE.polygon == map_value_type_indicator ?
                        Ext.getCmp('indicator_cb').getValue() : Ext.getCmp('dataelement_cb').getValue();
                    var dataUrl = VALUETYPE.polygon == map_value_type_indicator ?
                        'getIndicatorMapValuesByMap' : 'getDataMapValuesByMap';
                    var periodId = Ext.getCmp('period_cb').getValue();
                    var mapLayerPath = choropleth.newUrl;
                    
                    Ext.Ajax.request({
                        url: path_mapping + dataUrl + type,
                        method: 'POST',
                        params: {id:indicatorOrDataElementId, periodId:periodId, mapLayerPath:mapLayerPath},
                        success: function(r) {
                            var mapvalues = Ext.util.JSON.decode(r.responseText).mapvalues;
                            EXPORTVALUES = getExportDataValueJSON(mapvalues);
                            var mv = new Array();
                            var mour = new Array();
                            var nameColumn = MAPDATA[thematicMap].nameColumn;
                            var options = {};
                            
                            if (mapvalues.length == 0) {
                                Ext.messageRed.msg( i18n_thematic_map, i18n_current_selection_no_data );
                                MASK.hide();
                                return;
                            }
                            
                            for (var i = 0; i < mapvalues.length; i++) {
                                mv[mapvalues[i].orgUnitName] = mapvalues[i].orgUnitName ? mapvalues[i].value : '';
                            }
                            
                            Ext.Ajax.request({
                                url: path_mapping + 'getAvailableMapOrganisationUnitRelations' + type,
                                method: 'POST',
                                params: { mapLayerPath: mapLayerPath },
                                success: function(r) {
                                    var relations = Ext.util.JSON.decode(r.responseText).mapOrganisationUnitRelations;
                                   
                                    for (var i = 0; i < relations.length; i++) {
                                        mour[relations[i].featureId] = relations[i].organisationUnit;
                                    }

                                    for (var j = 0; j < FEATURE[thematicMap].length; j++) {
                                        FEATURE[thematicMap][j].attributes.value = mv[mour[FEATURE[thematicMap][j].attributes[nameColumn]]] || 0;
                                    }
                                    
                                    choropleth.applyValues();
                                }
                            });
                        }
                    });
                }
            });
        }
    },
            
    onRender: function(ct, position) {
        mapfish.widgets.geostat.Choropleth.superclass.onRender.apply(this, arguments);
        if(this.loadMask){
            this.loadMask = new Ext.LoadMask(this.bwrap,
                    this.loadMask);
            this.loadMask.show();
        }

        var coreOptions = {
            'layer': this.layer,
            'format': this.format,
            'url': this.url,
            'requestSuccess': this.requestSuccess.createDelegate(this),
            'requestFailure': this.requestFailure.createDelegate(this),
            'featureSelection': this.featureSelection,
            'nameAttribute': this.nameAttribute,
            'legendDiv': this.legendDiv,
            'labelGenerator': this.labelGenerator
        };

        this.coreComp = new mapfish.GeoStat.Choropleth(this.map, coreOptions);
    }   
});

Ext.reg('choropleth', mapfish.widgets.geostat.Choropleth);