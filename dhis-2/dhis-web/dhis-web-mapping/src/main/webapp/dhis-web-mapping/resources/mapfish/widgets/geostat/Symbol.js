﻿/*
 * Copyright (C) 2007-2008  Camptocamp|
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
 * @requires core/GeoStat/Symbol.js
 * @requires core/Color.js
 */

Ext.namespace('mapfish.widgets', 'mapfish.widgets.geostat');

mapfish.widgets.geostat.Symbol = Ext.extend(Ext.Panel, {

    layer: null,

    format: null,

    url: null,

    featureSelection: true,

    nameAttribute: null,

    indicator: null,

    indicatorText: null,

    coreComp: null,

    classificationApplied: false,

    ready: false,

    border: false,

    loadMask: false,

    labelGenerator: null,

    colorInterpolation: false,

    newUrl: false,

    legend: false,

	imageLegend: false,

	bounds: false,

    mapView: false,

    mapData: false,
    
    labels: false,
    
    valueType: false,
    
    selectFeatures: false,
    
    organisationUnitSelection: false,
    
    iconCombos: [],
    
    stores: false,
    
    infrastructuralPeriod: false,
    
    featureOptions: {},

    cmp: {},
    
    initComponent: function() {
        
        this.initProperties();
        
        this.createItems();
        
        this.addItems();
        
        this.createSelectFeatures();
        
		mapfish.widgets.geostat.Symbol.superclass.initComponent.apply(this);
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
        OpenLayers.Console.error(G.i18n.ajax_request_failed);
    },
    
    getColors: function() {
        var startColor = new mapfish.ColorRgb();
        startColor.setFromHex(this.form.findField('startcolor').getValue());
        var endColor = new mapfish.ColorRgb();
        endColor.setFromHex(this.form.findField('endcolor').getValue());
        return [startColor, endColor];
    },
    
    initProperties: function() {
        this.legend = {
            value: G.conf.map_legendset_type_automatic,
            method: G.conf.classify_by_equal_intervals,
            classes: 5,
            reset: function() {
                this.value = G.conf.map_legendset_type_automatic;
                this.method = G.conf.classify_by_equal_intervals;
                this.classes = 5;
            }
        };
        
        this.organisationUnitSelection = {
            parent: {
                id: null,
                name: null,
                level: null
            },
            level: {
                level: null,
                name: null
            },
            setValues: function(pid, pn, pl, ll, ln) {
                this.parent.id = pid || this.parent.id;
                this.parent.name = pn || this.parent.name;
                this.parent.level = pl || this.parent.level;
                this.level.level = ll || this.level.level;
                this.level.name = ln || this.level.name;
            },
            getValues: function() {
                return {
                    parent: {
                        id: this.parent.id,
                        name: this.parent.name,
                        level: this.parent.level
                    },
                    level: {
                        level: this.level.level,
                        name: this.level.name
                    }                    
                };
            },
            setValuesOnDrillDown: function(pid, pn) {
                this.parent.id = pid;
                this.parent.name = pn;
                this.parent.level = this.level.level;
                this.level.level++;
                this.level.name = G.stores.organisationUnitLevel.getAt(
                    G.stores.organisationUnitLevel.find('level', this.level.level)).data.name;
                
                return [this.parent.name, this.level.name];
            }                
        };
        
        this.valueType = {
            value: G.conf.map_value_type_indicator,
            setIndicator: function() {
                this.value = G.conf.map_value_type_indicator;
            },
            setDatElement: function() {
                this.value = G.conf.map_value_type_dataelement;
            },
            isIndicator: function() {
                return this.value == G.conf.map_value_type_indicator;
            },
            isDataElement: function() {
                return this.value == G.conf.map_value_type_dataelement;
            }
        };
        
        this.stores = {
            icon: new Ext.data.ArrayStore({
                fields: ['name', 'css'],
                data: [
                    ['0','ux-ic-icon-groupset-type-0'],
                    ['1','ux-ic-icon-groupset-type-1'],
                    ['2','ux-ic-icon-groupset-type-2'],
                    ['3','ux-ic-icon-groupset-type-3'],
                    ['4','ux-ic-icon-groupset-type-4'],
                    ['5','ux-ic-icon-groupset-type-5'],
                    ['6','ux-ic-icon-groupset-type-6'],
                    ['7','ux-ic-icon-groupset-type-7'],
                    ['8','ux-ic-icon-groupset-type-8'],
                    ['9','ux-ic-icon-groupset-type-9'],
                    ['10','ux-ic-icon-groupset-type-10'],
                    ['11','ux-ic-icon-groupset-type-11'],
                    ['12','ux-ic-icon-groupset-type-12']
                ]
            }),
            infrastructuralDataElementMapValue: new Ext.data.JsonStore({
                url: G.conf.path_mapping + 'getInfrastructuralDataElementMapValues' + G.conf.type,
                root: 'mapValues',
                fields: ['dataElementName', 'value'],
                sortInfo: {field: 'dataElementName', direction: 'ASC'},
                autoLoad: false,
                isLoaded: false,
                listeners: {
                    'load': G.func.storeLoadListener
                }
            })
        };
    },
    
    createItems: function() {
        
        this.cmp.groupSet = new Ext.form.ComboBox({
            fieldLabel: G.i18n.groupset,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: G.conf.emptytext,
            labelSeparator: G.conf.labelseparator,
            selectOnFocus: true,
            width: G.conf.combo_width,
            currentValue: false,
            store: G.stores.groupSet,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        if (cb.currentValue != cb.getValue() && cb.getRawValue() == 'Type') {
                            cb.currentValue = cb.getValue();
                            G.stores.groupsByGroupSet.setBaseParam('id', cb.getValue());
                            G.stores.groupsByGroupSet.load({scope: this, callback: function() {
                                this.cmp.group.removeAll();
                                
                                for (var i = 0; i < G.stores.groupsByGroupSet.getTotalCount(); i++) {
                                    var combo = {
                                        fieldLabel: G.stores.groupsByGroupSet.getAt(i).data.name,
                                        value: i
                                    };
                                    this.cmp.group.add(combo);
                                    this.cmp.group.doLayout();
                                }
                                
                                this.classify(false, true);
                            }});
                        }
                        else if (cb.getRawValue() != 'Type') {
                            cb.currentValue = cb.getValue();
                            this.cmp.group.removeAll();
                            this.cmp.group.doLayout();
                        }
                        
                        this.window.cmp.reset.enable();
                    }
                }
            }
        });
        
        this.cmp.group = new Ext.Panel({
            layout: 'form',
            width: 257,
            height: 344,
            bodyStyle: 'padding:8px 0px 8px 18px;overflow-x:hidden;overflow-y:auto;',
            labelWidth: 170,
            defaults: {
                xtype: 'combo',
                plugins: new Ext.ux.plugins.IconCombo(),
                valueField: 'name',
                displayField: 'css',
                iconClsField: 'css',
                editable: false,
                triggerAction: 'all',
                mode: 'local',
                labelStyle: 'color:#000;',
                labelSeparator: G.conf.labelseparator,
                width: G.conf.combo_number_width_small,
                listWidth: G.conf.combo_number_width_small,
                store: this.stores.icon,
                listeners: {
                    'select': {
                        scope: this,
                        fn: function() {
                            this.classify(false, true);
                        }
                    }
                }
            }
        });
        
        this.cmp.level = new Ext.form.ComboBox({
            fieldLabel: G.i18n.level,
            editable: false,
            valueField: 'level',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            selectOnFocus: true,
            fieldLabel: G.i18n.level,
            width: G.conf.combo_width,
            store: G.stores.organisationUnitLevel,
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
                        this.formValidation.validateForm.call(this);
                        
                        this.window.cmp.reset.enable();
                    }
                }
            }
        });
        
        this.cmp.parent = new Ext.tree.TreePanel({
            bodyStyle: 'padding-left:2px; background-color:#fff',
            height: 315,
            width: 255,
            autoScroll: true,
            lines: false,
            loader: new Ext.tree.TreeLoader({
                dataUrl: G.conf.path_mapping + 'getOrganisationUnitChildren' + G.conf.type
            }),
            root: {
                id: G.system.rootNode.id,
                text: G.system.rootNode.name,
                level: G.system.rootNode.level,
                hasChildrenWithCoordinates: G.system.rootNode.hasChildrenWithCoordinates,
                nodeType: 'async',
                draggable: false,
                expanded: true
            },
            widget: this,
            isSelected: false,
            reset: function() {
                if (this.getSelectionModel().getSelectedNode()) {
                    this.getSelectionModel().getSelectedNode().unselect();
                }                
                this.collapseAll();
                this.getRootNode().expand();
                this.isSelected = false;
                this.widget.window.cmp.apply.disable();
            },
            listeners: {
                'click': {
                    scope: this,
                    fn: function(n) {
                        this.cmp.parent.selectedNode = n;
                        this.cmp.parent.isSelected = true;
                        this.formValidation.validateForm.call(this);
                        
                        this.window.cmp.reset.enable();
                    }
                }
            }
        });
    },
    
    addItems: function() {    
        
        this.items = [
            {
                xtype: 'panel',
                layout: 'column',
                width: 570,
                items: [
                    {
                        xtype: 'panel',
                        layout: 'form',
                        width: 270,
                        items: [
                            { html: '<div class="window-info">Symbolizer by group / group set</div>' },                            
                            this.cmp.groupSet,
                            { html: '<div class="thematic-br"></div>' },
                            this.cmp.group
                        ]
                    },
                    {
                        xtype: 'panel',
                        width: 270,
                        bodyStyle: 'padding:0 0 0 8px;',
                        items: [
                            { html: '<div class="window-info">' + G.i18n.organisation_unit_level + ' (facility)</div>' },                            
                            {
                                xtype: 'panel',
                                layout: 'form',
                                items: [
                                    this.cmp.level
                                ]
                            },                            
                            { html: '<div class="thematic-br"></div><div class="thematic-br"></div>' },                            
                            { html: '<div class="window-info">Parent organisation unit</div>' },                            
                            this.cmp.parent
                        ]
                    }
                ]
            }
        ];
    },
    
    createSelectFeatures: function() {
        var scope = this;
        
        var onHoverSelect = function onHoverSelect(feature) {
            if (feature.attributes.name) {
                document.getElementById('featuredatatext').innerHTML = '<div style="color:black">' + feature.attributes.name + '</div><div style="color:#555">' + feature.attributes.type + '</div>';
            }
            else {
                document.getElementById('featuredatatext').innerHTML = '';
            }
        };
        
        var onHoverUnselect = function onHoverUnselect(feature) {
            if (feature.attributes.name) {
                document.getElementById('featuredatatext').innerHTML = '<div style="color:#666">' + G.i18n.no_feature_selected + '</div>';
            }
            else {
                document.getElementById('featuredatatext').innerHTML = '';
            }
        };
        
        var onClickSelect = function onClickSelect(feature) {
            if (feature.geometry.CLASS_NAME == G.conf.map_feature_type_point_class_name) {
                if (scope.featureOptions.menu) {
                    scope.featureOptions.menu.destroy();
                }
                
                scope.featureOptions.menu = new Ext.menu.Menu({
                    showInfo: function() {
                        if (scope.featureOptions.info) {
                            scope.featureOptions.info.destroy();
                        }
                        
                        scope.featureOptions.info = new Ext.Window({
                            title: '<span class="window-information-title">' + feature.attributes.name + '</span>',
                            layout: 'table',
                            width: G.conf.window_width + 178,
                            height: G.util.getMultiSelectHeight() + 100,
                            bodyStyle: 'background-color:#fff',
                            defaults: {
                                bodyStyle: 'vertical-align:top',
                                labelSeparator: G.conf.labelseparator,
                                emptyText: G.conf.emptytext
                            },
                            layoutConfig: {
                                columns: 2
                            },
                            items: [
                                {
                                    xtype: 'panel',
                                    layout: 'anchor',
                                    bodyStyle: 'padding:8px 4px 8px 8px',
                                    width: 160,
                                    items: [
                                        {html: '<div class="window-info">' + G.i18n.type + '<p style="font-weight:normal">' + feature.attributes.type + '</p></div>'},
                                        {html: '<div class="window-info">' + G.i18n.code + '<p style="font-weight:normal">' + feature.attributes.code + '</p></div>'},
                                        {html: '<div class="window-info">' + G.i18n.address + '<p style="font-weight:normal">' + feature.attributes.ad + '</p></div>'},
                                        {html: '<div class="window-info">' + G.i18n.contact_person + '<p style="font-weight:normal">' + feature.attributes.cp + '</p></div>'},
                                        {html: '<div class="window-info">' + G.i18n.email + '<p style="font-weight:normal">' + feature.attributes.em + '</p></div>'},
                                        {html: '<div class="window-info">' + G.i18n.phone_number + '<p style="font-weight:normal">' + feature.attributes.pn + '</p></div>'}
                                    ]
                                },
                                {
                                    xtype: 'form',
                                    bodyStyle: 'padding:8px 8px 8px 4px',
                                    width: G.conf.window_width + 20,
                                    labelWidth: G.conf.label_width,
                                    items: [
                                        {html: '<div class="window-info">' + G.i18n.infrastructural_data + '</div>'},
                                        {
                                            xtype: 'combo',
                                            name: 'period',
                                            fieldLabel: G.i18n.period,
                                            typeAhead: true,
                                            editable: false,
                                            valueField: 'id',
                                            displayField: 'name',
                                            mode: 'remote',
                                            forceSelection: true,
                                            triggerAction: 'all',
                                            selectOnFocus: true,
                                            width: G.conf.combo_width,
                                            store: G.stores.infrastructuralPeriodsByType,
                                            lockPosition: false,
                                            listeners: {
                                                'select': function(cb) {
                                                    scope.infrastructuralPeriod = cb.getValue();
                                                    scope.stores.infrastructuralDataElementMapValue.setBaseParam('periodId', cb.getValue());
                                                    scope.stores.infrastructuralDataElementMapValue.setBaseParam('organisationUnitId', feature.attributes.id);
                                                    scope.stores.infrastructuralDataElementMapValue.load();
                                                }
                                            }
                                        },
                                        {html: '<div style="padding:4px 0 0 0"></div>'},
                                        {
                                            xtype: 'grid',
                                            height: G.util.getMultiSelectHeight(),
                                            width: 242,
                                            cm: new Ext.grid.ColumnModel({
                                                columns: [
                                                    {id: 'dataElementName', header: 'Data element', dataIndex: 'dataElementName', sortable: true, width: 150},
                                                    {id: 'value', header: 'Value', dataIndex: 'value', sortable: true, width: 50}
                                                ]
                                            }),
                                            disableSelection: true,
                                            viewConfig: {forceFit: true},
                                            store: scope.stores.infrastructuralDataElementMapValue
                                        }
                                    ]
                                }
                            ]
                        });
    
                        if (scope.infrastructuralPeriod) {
                            scope.featureOptions.info.find('name', 'period')[0].setValue(scope.infrastructuralPeriod);
                            scope.stores.infrastructuralDataElementMapValue.setBaseParam('periodId', scope.infrastructuralPeriod);
                            scope.stores.infrastructuralDataElementMapValue.setBaseParam('organisationUnitId', feature.attributes.id);
                            scope.stores.infrastructuralDataElementMapValue.load();
                        }
                        scope.featureOptions.info.setPagePosition(Ext.getCmp('east').x - (scope.featureOptions.info.width + 15), Ext.getCmp('center').y + 41);
                        scope.featureOptions.info.show();
                        scope.featureOptions.menu.destroy();
                    },
                    showRelocate: function() {
                        if (scope.featureOptions.coordinate) {
                            scope.featureOptions.coordinate.destroy();
                        }
                        
                        scope.featureOptions.coordinate = new Ext.Window({
                            title: '<span class="window-relocate-title">' + feature.attributes.name + '</span>',
                            layout: 'fit',
                            width: G.conf.window_width,
                            height: 95,
                            items: [
                                {
                                    xtype: 'panel',
                                    bodyStyle: 'padding:8px',
                                    items: [
                                        {html: G.i18n.select_new_location_on_map}
                                    ]
                                }
                            ],
                            bbar: [
                                '->',
                                {
                                    xtype: 'button',
                                    iconCls: 'icon-cancel',
                                    hideLabel: true,
                                    text: G.i18n.cancel,
                                    handler: function() {
                                        G.vars.relocate.active = false;
                                        scope.featureOptions.coordinate.destroy();
                                        document.getElementById('OpenLayers.Map_3_OpenLayers_ViewPort').style.cursor = 'auto';
                                    }
                                }
                            ],
                            listeners: {
                                'close': function() {
                                    G.vars.relocate.active = false;
                                    document.getElementById('OpenLayers.Map_3_OpenLayers_ViewPort').style.cursor = 'auto';
                                }
                            }
                        });
                        scope.featureOptions.coordinate.setPagePosition(Ext.getCmp('east').x - (scope.featureOptions.coordinate.width + 15), Ext.getCmp('center').y + 41);
                        scope.featureOptions.coordinate.show();                        
                    },
                    items: [
                        {
                            text: G.i18n.show_information_sheet,
                            iconCls: 'menu-featureoptions-info',
                            handler: function(item) {
                                if (G.stores.infrastructuralPeriodsByType.isLoaded) {
                                    item.parentMenu.showInfo();
                                }
                                else {
                                    G.stores.infrastructuralPeriodsByType.setBaseParam('name', G.system.infrastructuralPeriodType);
                                    G.stores.infrastructuralPeriodsByType.load({callback: function() {
                                        item.parentMenu.showInfo();
                                    }});
                                }
                            }
                        },
                        {
                            text: G.i18n.relocate,
                            iconCls: 'menu-featureoptions-relocate',
                            disabled: !G.user.isAdmin,
                            handler: function(item) {
                                G.vars.relocate.active = true;
                                G.vars.relocate.widget = scope;
                                G.vars.relocate.feature = feature;
                                document.getElementById('OpenLayers.Map_3_OpenLayers_ViewPort').style.cursor = 'crosshair';
                                item.parentMenu.showRelocate();
                            }
                        }
                    ]
                });
                scope.featureOptions.menu.showAt([G.vars.mouseMove.x, G.vars.mouseMove.y]);
            }
            else {
                if (feature.attributes.hasChildrenWithCoordinates) {
                    if (G.vars.locateFeatureWindow) {
                        G.vars.locateFeatureWindow.destroy();
                    }
                             
                    scope.updateValues = true;
                    scope.isDrillDown = true;
                    
                    function organisationUnitLevelCallback() {
                        var names = this.organisationUnitSelection.setValuesOnDrillDown(feature.attributes.id, feature.attributes.name);
                        this.form.findField('boundary').setValue(names[0]);
                        this.form.findField('level').setValue(names[1]);
                        this.loadGeoJson();
                    }
                    
                    if (G.stores.organisationUnitLevel.isLoaded) {
                        organisationUnitLevelCallback.call(scope);
                    }
                    else {
                        G.stores.organisationUnitLevel.load({scope: scope, callback: function() {
                            organisationUnitLevelCallback.call(this);
                        }});
                    }
                }
                else {
                    Ext.message.msg(false, G.i18n.no_coordinates_found);
                }
            }
        };
        
        this.selectFeatures = new OpenLayers.Control.newSelectFeature(
            this.layer, {
                onHoverSelect: onHoverSelect,
                onHoverUnselect: onHoverUnselect,
                onClickSelect: onClickSelect
            }
        );
        
        G.vars.map.addControl(this.selectFeatures);
        this.selectFeatures.activate();
    },
    
    formValidation: {
        validateForm: function(exception) {
            
            if (!this.cmp.groupSet.getValue()) {
                if (exception) {
                    Ext.message.msg(false, G.i18n.form_is_not_complete);
                }
                this.window.cmp.apply.disable();
                return false;
            }

            if (!this.cmp.parent.selectedNode || !this.cmp.level.getValue()) {
                if (exception) {
                    Ext.message.msg(false, G.i18n.form_is_not_complete);
                }
                this.window.cmp.apply.disable();
                return false;
            }
            
            if (this.cmp.parent.selectedNode.attributes.level > this.cmp.level.getValue()) {
                if (exception) {
                    Ext.message.msg(false, 'Invalid parent organisation unit');
                }
                this.window.cmp.apply.disable();
                return false;
            }
            
            if (this.cmp.parent.isSelected) {
                this.window.cmp.apply.enable();
            }
            
            return true;
        }
    },
    
    formValues: { //todo
		getAllValues: function() {
			return {
				featureType: G.conf.map_feature_type_point,
                mapDateType: G.system.mapDateType.value,
				parentOrganisationUnitId: this.organisationUnitSelection.parent.id,
                parentOrganisationUnitLevel: this.organisationUnitSelection.parent.level,
                parentOrganisationUnitName: this.organisationUnitSelection.parent.name,
				organisationUnitLevel: this.organisationUnitSelection.level.level,
                organisationUnitLevelName: this.organisationUnitSelection.level.name,
				mapLegendType: this.form.findField('maplegendtype').getValue(),
				method: this.legend.value == G.conf.map_legendset_type_automatic ? this.form.findField('method').getValue() : null,
				classes: this.legend.value == G.conf.map_legendset_type_automatic ? this.form.findField('classes').getValue() : null,
				bounds: this.legend.value == G.conf.map_legendset_type_automatic && this.legend.method == G.conf.classify_with_bounds ? this.form.findField('bounds').getValue() : null,
				colorLow: this.legend.value == G.conf.map_legendset_type_automatic ? this.form.findField('startcolor').getValue() : null,
				colorHigh: this.legend.value == G.conf.map_legendset_type_automatic ? this.form.findField('endcolor').getValue() : null,
                mapLegendSetId: this.legend.value == G.conf.map_legendset_type_predefined ? this.form.findField('maplegendset').getValue() : null,
				radiusLow: this.form.findField('radiuslow').getValue(),
				radiusHigh: this.form.findField('radiushigh').getValue(),
				longitude: G.vars.map.getCenter().lon,
				latitude: G.vars.map.getCenter().lat,
				zoom: parseFloat(G.vars.map.getZoom())
			};
		},
        
        getLegendInfo: function() {
            return {
                map: this.organisationUnitSelection.level.name + ' / ' + this.organisationUnitSelection.parent.name
            };
        },
        
        clearForm: function(clearLayer) {
            this.cmp.groupSet.clearValue();
            this.cmp.groupSet.currentValue = null;
            
            this.cmp.group.removeAll();
            this.cmp.group.doLayout();
            
            this.cmp.level.clearValue();
            this.cmp.parent.reset();
            
            this.window.cmp.apply.disable();
            this.window.cmp.reset.disable();
            
            if (clearLayer) {            
                document.getElementById(this.legendDiv).innerHTML = '';                
                this.layer.destroyFeatures();
                this.layer.setVisibility(false);
            }
        }
	},
    
    loadGeoJson: function() {
        G.vars.mask.msg = G.i18n.loading_geojson;
        G.vars.mask.show();
        G.vars.activeWidget = this;
        this.updateValues = true;
        
        this.setUrl(G.conf.path_mapping + 'getGeoJson.action?' +
            'parentId=' + this.organisationUnitSelection.parent.id +
            '&level=' + this.organisationUnitSelection.level.level
        );
    },

    classify: function(exception, lockPosition) {
        if (this.formValidation.validateForm.apply(this, [exception])) {
            
            G.vars.lockPosition = lockPosition;
            
            for (var i = 0; i < this.layer.features.length; i++) {
                this.layer.features[i].attributes.labelString = this.layer.features[i].attributes.name;
                this.layer.features[i].attributes.name = G.util.cutString(this.layer.features[i].attributes.name, 30);
            }
             
            this.applyValues();
        }
    },

    applyValues: function() {
		var options = {indicator: this.cmp.groupSet.getRawValue().toLowerCase()};
        
        G.vars.activeWidget = this;
		this.coreComp.updateOptions(options);
        this.coreComp.applyClassification(this.form, this);
        this.classificationApplied = true;
        
        G.vars.mask.hide();
    },
    
    onRender: function(ct, position) {
        mapfish.widgets.geostat.Symbol.superclass.onRender.apply(this, arguments);
        if (this.loadMask) {
            this.loadMask = new Ext.LoadMask(this.bwrap, this.loadMask);
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

        this.coreComp = new mapfish.GeoStat.Symbol(this.map, coreOptions);
        
        if (G.vars.parameter.id) {
            G.util.expandWidget(this);
			G.vars.parameter = false;
		}
    }
});

Ext.reg('symbol', mapfish.widgets.geostat.Symbol);
