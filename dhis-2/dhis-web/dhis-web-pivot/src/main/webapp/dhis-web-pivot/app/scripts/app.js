Ext.onReady( function() {
	var NS = PT,

		LayoutWindow,
		OptionsWindow,
		FavoriteWindow,
		SharingWindow,
		InterpretationWindow,

		extendCore,
		createViewport,
		update,
		initialize,
		ns = {
			core: {},
			app: {}
		};

	// set config
	(function() {

		// ext configuration
		Ext.QuickTips.init();

		Ext.override(Ext.LoadMask, {
			onHide: function() {
				this.callParent();
			}
		});

		// right click handler
		document.body.oncontextmenu = function() {
			return false;
		};
	}());

	// constructors
	LayoutWindow = function() {
		var dimension,
			dimensionStore,
			row,
			rowStore,
			col,
			colStore,
			filter,
			filterStore,
			value,

			getData,
			getStore,
			getStoreKeys,
			getCmpHeight,
			getSetup,

			dimensionPanel,
			selectPanel,
			window,

			margin = 2,
			defaultWidth = 160,
			defaultHeight = 158,
			maxHeight = (ns.viewport.getHeight() - 100) / 2,

			dimConf = ns.conf.finals.dimension;

		getData = function(all) {
			var data = [];

			if (all) {
				data.push({id: dimConf.data.dimensionName, name: dimConf.data.name});
			}

			data.push({id: dimConf.category.dimensionName, name: dimConf.category.name});

			if (all) {
				data.push({id: dimConf.period.dimensionName, name: dimConf.period.name});
				data.push({id: dimConf.organisationUnit.dimensionName, name: dimConf.organisationUnit.name});
			}

			return data.concat(Ext.clone(ns.init.dimensions));
		};

		getStore = function(data) {
			var config = {};

			config.fields = ['id', 'name'];

			if (data) {
				config.data = data;
			}

			config.getDimensionNames = function() {
				var dimensionNames = [];

				this.each(function(r) {
					dimensionNames.push(r.data.id);
				});

				return Ext.clone(dimensionNames);
			};

			return Ext.create('Ext.data.Store', config);
		};

		getStoreKeys = function(store) {
			var keys = [],
				items = store.data.items;

			if (items) {
				for (var i = 0; i < items.length; i++) {
					keys.push(items[i].data.id);
				}
			}

			return keys;
		};

		dimensionStore = getStore(getData());
		dimensionStore.reset = function(all) {
			dimensionStore.removeAll();
			dimensionStore.add(getData(all));
		};
		ns.viewport.dimensionStore = dimensionStore;

		rowStore = getStore();
		ns.viewport.rowStore = rowStore;
		rowStore.add({id: dimConf.period.dimensionName, name: dimConf.period.name});

		colStore = getStore();
		ns.viewport.colStore = colStore;
		colStore.add({id: dimConf.data.dimensionName, name: dimConf.data.name});

		filterStore = getStore();
		ns.viewport.filterStore = filterStore;
		filterStore.add({id: dimConf.organisationUnit.dimensionName, name: dimConf.organisationUnit.name});

		getCmpHeight = function() {
			var size = dimensionStore.totalCount,
				expansion = 10,
				height = defaultHeight,
				diff;

			if (size > 10) {
				diff = size - 10;
				height += (diff * expansion);
			}

			height = height > maxHeight ? maxHeight : height;

			return height;
		};

		dimension = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-leftright',
			width: defaultWidth,
			height: (getCmpHeight() * 2) + margin,
			style: 'margin-right:' + margin + 'px; margin-bottom:0px',
			valueField: 'id',
			displayField: 'name',
			dragGroup: 'layoutDD',
			dropGroup: 'layoutDD',
			ddReorder: false,
			store: dimensionStore,
			tbar: {
				height: 25,
				items: {
					xtype: 'label',
					text: NS.i18n.dimensions,
					cls: 'ns-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.store.on('add', function() {
						Ext.defer( function() {
							ms.boundList.getSelectionModel().deselectAll();
						}, 10);
					});
				}
			}
		});

		row = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-leftright',
			width: defaultWidth,
			height: getCmpHeight(),
			style: 'margin-bottom:0px',
			valueField: 'id',
			displayField: 'name',
			dragGroup: 'layoutDD',
			dropGroup: 'layoutDD',
			store: rowStore,
			tbar: {
				height: 25,
				items: {
					xtype: 'label',
					text: NS.i18n.row,
					cls: 'ns-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.boundList.on('itemdblclick', function(view, record) {
						ms.store.remove(record);
						dimensionStore.add(record);
					});

					ms.store.on('add', function() {
						Ext.defer( function() {
							ms.boundList.getSelectionModel().deselectAll();
						}, 10);
					});
				}
			}
		});

		col = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-leftright',
			width: defaultWidth,
			height: getCmpHeight(),
			style: 'margin-bottom:' + margin + 'px',
			valueField: 'id',
			displayField: 'name',
			dragGroup: 'layoutDD',
			dropGroup: 'layoutDD',
			store: colStore,
			tbar: {
				height: 25,
				items: {
					xtype: 'label',
					text: NS.i18n.column,
					cls: 'ns-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.boundList.on('itemdblclick', function(view, record) {
						ms.store.remove(record);
						dimensionStore.add(record);
					});

					ms.store.on('add', function() {
						Ext.defer( function() {
							ms.boundList.getSelectionModel().deselectAll();
						}, 10);
					});
				}
			}
		});

		filter = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-leftright',
			width: defaultWidth,
			height: getCmpHeight(),
			style: 'margin-right:' + margin + 'px; margin-bottom:' + margin + 'px',
			valueField: 'id',
			displayField: 'name',
			dragGroup: 'layoutDD',
			dropGroup: 'layoutDD',
			store: filterStore,
			tbar: {
				height: 25,
				items: {
					xtype: 'label',
					text: NS.i18n.filter,
					cls: 'ns-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.boundList.on('itemdblclick', function(view, record) {
						ms.store.remove(record);
						dimensionStore.add(record);
					});

					ms.store.on('add', function() {
						Ext.defer( function() {
							ms.boundList.getSelectionModel().deselectAll();
						}, 10);
					});
				}
			}
		});

		selectPanel = Ext.create('Ext.panel.Panel', {
			bodyStyle: 'border:0 none',
			items: [
				{
					layout: 'column',
					bodyStyle: 'border:0 none',
					items: [
						filter,
						col
					]
				},
				{
					layout: 'column',
					bodyStyle: 'border:0 none',
					items: [
						row
					]
				}
			]
		});

		getSetup = function() {
			return {
				col: getStoreKeys(colStore),
				row: getStoreKeys(rowStore),
				filter: getStoreKeys(filterStore)
			};
		};

		window = Ext.create('Ext.window.Window', {
			title: NS.i18n.table_layout,
			bodyStyle: 'background-color:#fff; padding:2px',
			closeAction: 'hide',
			autoShow: true,
			modal: true,
			resizable: false,
			getSetup: getSetup,
			dimensionStore: dimensionStore,
			rowStore: rowStore,
			colStore: colStore,
			filterStore: filterStore,
			hideOnBlur: true,
			items: {
				layout: 'column',
				bodyStyle: 'border:0 none',
				items: [
					dimension,
					selectPanel
				]
			},
			bbar: [
				'->',
				{
					text: NS.i18n.hide,
					listeners: {
						added: function(b) {
							b.on('click', function() {
								window.hide();
							});
						}
					}
				},
				{
					text: '<b>' + NS.i18n.update + '</b>',
					listeners: {
						added: function(b) {
							b.on('click', function() {
								ns.viewport.updateViewport();
								window.hide();
							});
						}
					}
				}
			],
			listeners: {
				show: function(w) {
					if (ns.viewport.layoutButton.rendered) {
						ns.util.window.setAnchorPosition(w, ns.viewport.layoutButton);

						if (!w.hasHideOnBlurHandler) {
							ns.util.window.addHideOnBlurHandler(w);
						}
					}
				}
			}
		});

		return window;
	};

	OptionsWindow = function() {
		var showTotals,
			showSubTotals,
			hideEmptyRows,
			showHierarchy,
			digitGroupSeparator,
			displayDensity,
			fontSize,
			reportingPeriod,
			organisationUnit,
			parentOrganisationUnit,

			data,
			style,
			parameters,

			comboboxWidth = 262,
			window;

		showTotals = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.show_totals,
			style: 'margin-bottom:4px',
			checked: true
		});
		ns.viewport.showTotals = showTotals;

		showSubTotals = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.show_subtotals,
			style: 'margin-bottom:4px',
			checked: true
		});
		ns.viewport.showSubTotals = showSubTotals;

		hideEmptyRows = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.hide_empty_rows,
			style: 'margin-bottom:4px'
		});
		ns.viewport.hideEmptyRows = hideEmptyRows;

		showHierarchy = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.show_hierarchy,
			style: 'margin-bottom:4px'
		});
		ns.viewport.showHierarchy = showHierarchy;

		displayDensity = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			style: 'margin-bottom:3px',
			width: comboboxWidth,
			labelWidth: 130,
			fieldLabel: NS.i18n.display_density,
			labelStyle: 'color:#333',
			queryMode: 'local',
			valueField: 'id',
			editable: false,
			value: 'normal',
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'text'],
				data: [
					{id: 'comfortable', text: NS.i18n.comfortable},
					{id: 'normal', text: NS.i18n.normal},
					{id: 'compact', text: NS.i18n.compact}
				]
			})
		});
		ns.viewport.displayDensity = displayDensity;

		fontSize = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			style: 'margin-bottom:3px',
			width: comboboxWidth,
			labelWidth: 130,
			fieldLabel: NS.i18n.font_size,
			labelStyle: 'color:#333',
			queryMode: 'local',
			valueField: 'id',
			editable: false,
			value: 'normal',
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'text'],
				data: [
					{id: 'large', text: NS.i18n.large},
					{id: 'normal', text: NS.i18n.normal},
					{id: 'small', text: NS.i18n.small_}
				]
			})
		});
		ns.viewport.fontSize = fontSize;

		digitGroupSeparator = Ext.create('Ext.form.field.ComboBox', {
			labelStyle: 'color:#333',
			cls: 'ns-combo',
			style: 'margin-bottom:3px',
			width: comboboxWidth,
			labelWidth: 130,
			fieldLabel: NS.i18n.digit_group_separator,
			queryMode: 'local',
			valueField: 'id',
			editable: false,
			value: 'space',
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'text'],
				data: [
					{id: 'comma', text: 'Comma'},
					{id: 'space', text: 'Space'},
					{id: 'none', text: 'None'}
				]
			})
		});
		ns.viewport.digitGroupSeparator = digitGroupSeparator;

		legendSet = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			style: 'margin-bottom:3px',
			width: comboboxWidth,
			labelWidth: 130,
			fieldLabel: NS.i18n.legend_set,
			valueField: 'id',
			displayField: 'name',
			editable: false,
			value: 0,
			store: ns.store.legendSet
		});
		ns.viewport.legendSet = legendSet;

		reportingPeriod = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.reporting_period,
			style: 'margin-bottom:4px',
		});
		ns.viewport.reportingPeriod = reportingPeriod;

		organisationUnit = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.organisation_unit,
			style: 'margin-bottom:4px',
		});
		ns.viewport.organisationUnit = organisationUnit;

		parentOrganisationUnit = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.parent_organisation_unit,
			style: 'margin-bottom:4px',
		});
		ns.viewport.parentOrganisationUnit = parentOrganisationUnit;

		regression = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.include_regression,
			style: 'margin-bottom:4px',
		});
		ns.viewport.regression = regression;

		cumulative = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.include_cumulative,
			style: 'margin-bottom:6px',
		});
		ns.viewport.cumulative = cumulative;

		sortOrder = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			style: 'margin-bottom:3px',
			width: 250,
			labelWidth: 130,
			fieldLabel: NS.i18n.sort_order,
			labelStyle: 'color:#333',
			queryMode: 'local',
			valueField: 'id',
			editable: false,
			value: 0,
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'text'],
				data: [
					{id: 0, text: NS.i18n.none},
					{id: 1, text: NS.i18n.low_to_high},
					{id: 2, text: NS.i18n.high_to_low}
				]
			})
		});
		ns.viewport.sortOrder = sortOrder;

		topLimit = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			style: 'margin-bottom:0',
			width: 250,
			labelWidth: 130,
			fieldLabel: NS.i18n.top_limit,
			labelStyle: 'color:#333',
			queryMode: 'local',
			valueField: 'id',
			editable: false,
			value: 0,
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'text'],
				data: [
					{id: 0, text: NS.i18n.none},
					{id: 5, text: 5},
					{id: 10, text: 10},
					{id: 20, text: 20},
					{id: 50, text: 50},
					{id: 100, text: 100}
				]
			})
		});
		ns.viewport.topLimit = topLimit;

		data = {
			bodyStyle: 'border:0 none',
			style: 'margin-left:14px',
			items: [
				showTotals,
				showSubTotals,
				hideEmptyRows
			]
		};

		organisationUnits = {
			bodyStyle: 'border:0 none',
			style: 'margin-left:14px',
			items: [
				showHierarchy
			]
		};

		style = {
			bodyStyle: 'border:0 none',
			style: 'margin-left:14px',
			items: [
				displayDensity,
				fontSize,
				digitGroupSeparator,
				legendSet
			]
		};

		parameters = {
			bodyStyle: 'border:0 none; background:transparent',
			style: 'margin-left:14px',
			items: [
				reportingPeriod,
				organisationUnit,
				parentOrganisationUnit,
				regression,
				cumulative,
				sortOrder,
				topLimit
			]
		};

		window = Ext.create('Ext.window.Window', {
			title: NS.i18n.table_options,
			bodyStyle: 'background-color:#fff; padding:5px',
			closeAction: 'hide',
			autoShow: true,
			modal: true,
			resizable: false,
			hideOnBlur: true,
			getOptions: function() {
				return {
					showTotals: showTotals.getValue(),
					showSubTotals: showSubTotals.getValue(),
					hideEmptyRows: hideEmptyRows.getValue(),
					showHierarchy: showHierarchy.getValue(),
					displayDensity: displayDensity.getValue(),
					fontSize: fontSize.getValue(),
					digitGroupSeparator: digitGroupSeparator.getValue(),
					legendSet: {id: legendSet.getValue()},
					reportingPeriod: reportingPeriod.getValue(),
					organisationUnit: organisationUnit.getValue(),
					parentOrganisationUnit: parentOrganisationUnit.getValue(),
					regression: regression.getValue(),
					cumulative: cumulative.getValue(),
					sortOrder: sortOrder.getValue(),
					topLimit: topLimit.getValue()
				};
			},
			setOptions: function(layout) {
				showTotals.setValue(Ext.isBoolean(layout.showTotals) ? layout.showTotals : true);
				showSubTotals.setValue(Ext.isBoolean(layout.showSubTotals) ? layout.showSubTotals : true);
				hideEmptyRows.setValue(Ext.isBoolean(layout.hideEmptyRows) ? layout.hideEmptyRows : false);
				showHierarchy.setValue(Ext.isBoolean(layout.showHierarchy) ? layout.showHierarchy : false);
				displayDensity.setValue(Ext.isString(layout.displayDensity) ? layout.displayDensity : 'normal');
				fontSize.setValue(Ext.isString(layout.fontSize) ? layout.fontSize : 'normal');
				digitGroupSeparator.setValue(Ext.isString(layout.digitGroupSeparator) ? layout.digitGroupSeparator : 'space');
				legendSet.setValue(Ext.isObject(layout.legendSet) && Ext.isString(layout.legendSet.id) ? layout.legendSet.id : 0);
				reportingPeriod.setValue(Ext.isBoolean(layout.reportingPeriod) ? layout.reportingPeriod : false);
				organisationUnit.setValue(Ext.isBoolean(layout.organisationUnit) ? layout.organisationUnit : false);
				parentOrganisationUnit.setValue(Ext.isBoolean(layout.parentOrganisationUnit) ? layout.parentOrganisationUnit : false);
				regression.setValue(Ext.isBoolean(layout.regression) ? layout.regression : false);
				cumulative.setValue(Ext.isBoolean(layout.cumulative) ? layout.cumulative : false);
				sortOrder.setValue(Ext.isNumber(layout.sortOrder) ? layout.sortOrder : 0);
				topLimit.setValue(Ext.isNumber(layout.topLimit) ? layout.topLimit : 0);
			},
			items: [
				{
					bodyStyle: 'border:0 none; color:#222; font-size:12px; font-weight:bold',
					style: 'margin-bottom:6px; margin-left:2px',
					html: NS.i18n.data
				},
				data,
				{
					bodyStyle: 'border:0 none; padding:7px'
				},
				{
					bodyStyle: 'border:0 none; color:#222; font-size:12px; font-weight:bold',
					style: 'margin-bottom:6px; margin-left:2px',
					html: NS.i18n.organisation_units
				},
				organisationUnits,
				{
					bodyStyle: 'border:0 none; padding:7px'
				},
				{
					bodyStyle: 'border:0 none; color:#222; font-size:12px; font-weight:bold',
					style: 'margin-bottom:6px; margin-left:2px',
					html: NS.i18n.style
				},
				style,
				{
					bodyStyle: 'border:0 none; padding:4px'
				},
				{
					bodyStyle: 'border:1px solid #d5d5d5; padding:5px; background-color:#f0f0f0',
					items: [
						{
							bodyStyle: 'border:0 none; padding:0 5px 6px 2px; background-color:transparent; color:#222; font-size:12px',
							html: '<b>' + NS.i18n.parameters + '</b> <span style="font-size:11px"> (' + NS.i18n.for_standard_reports_only + ')</span>'
						},
						parameters
					]
				}
			],
			bbar: [
				'->',
				{
					text: NS.i18n.hide,
					handler: function() {
						window.hide();
					}
				},
				{
					text: '<b>' + NS.i18n.update + '</b>',
					handler: function() {
						ns.viewport.updateViewport();
						window.hide();
					}
				}
			],
			listeners: {
				show: function(w) {
					if (ns.viewport.optionsButton.rendered) {
						ns.util.window.setAnchorPosition(w, ns.viewport.optionsButton);

						if (!w.hasHideOnBlurHandler) {
							ns.util.window.addHideOnBlurHandler(w);
						}
					}

					if (!legendSet.store.isLoaded) {
						legendSet.store.load();
					}
				}
			}
		});

		return window;
	};

	FavoriteWindow = function() {

		// Objects
		var NameWindow,

		// Instances
			nameWindow,

		// Functions
			getBody,

		// Components
			addButton,
			searchTextfield,
			grid,
			prevButton,
			nextButton,
			tbar,
			bbar,
			info,
			nameTextfield,
			createButton,
			updateButton,
			cancelButton,
			favoriteWindow,

		// Vars
			windowWidth = 500,
			windowCmpWidth = windowWidth - 22;

		ns.store.reportTable.on('load', function(store, records) {
			var pager = store.proxy.reader.jsonData.pager;

			info.setText('Page ' + pager.page + ' of ' + pager.pageCount);

			prevButton.enable();
			nextButton.enable();

			if (pager.page === 1) {
				prevButton.disable();
			}

			if (pager.page === pager.pageCount) {
				nextButton.disable();
			}
		});

		getBody = function() {
			var favorite,
				dimensions;

			if (ns.layout) {
				favorite = Ext.clone(ns.layout);
				dimensions = [].concat(favorite.columns || [], favorite.rows || [], favorite.filters || []);

				// Server sync
				favorite.totals = favorite.showTotals;
				delete favorite.showTotals;

				favorite.subtotals = favorite.showSubTotals;
				delete favorite.showSubTotals;

				favorite.reportParams = {
					paramReportingPeriod: favorite.reportingPeriod,
					paramOrganisationUnit: favorite.organisationUnit,
					paramParentOrganisationUnit: favorite.parentOrganisationUnit
				};
				delete favorite.reportingPeriod;
				delete favorite.organisationUnit;
				delete favorite.parentOrganisationUnit;

				delete favorite.parentGraphMap;

				// Replace operand id characters
				for (var i = 0; i < dimensions.length; i++) {
					if (dimensions[i].dimension === ns.conf.finals.dimension.operand.objectName) {
						for (var j = 0; j < dimensions[i].items.length; j++) {
							dimensions[i].items[j].id = dimensions[i].items[j].id.replace('-', '.');
						}
					}
				}
			}

			return favorite;
		};

		NameWindow = function(id) {
			var window,
				record = ns.store.reportTable.getById(id);

			nameTextfield = Ext.create('Ext.form.field.Text', {
				height: 26,
				width: 371,
				fieldStyle: 'padding-left: 6px; border-radius: 1px; border-color: #bbb; font-size:11px',
				style: 'margin-bottom:0',
				emptyText: 'Favorite name',
				value: id ? record.data.name : '',
				listeners: {
					afterrender: function() {
						this.focus();
					}
				}
			});

			createButton = Ext.create('Ext.button.Button', {
				text: NS.i18n.create,
				handler: function() {
					var favorite = getBody();
					favorite.name = nameTextfield.getValue();

					//tmp
					//delete favorite.legendSet;

					if (favorite && favorite.name) {
						Ext.Ajax.request({
							url: ns.init.contextPath + '/api/reportTables/',
							method: 'POST',
							headers: {'Content-Type': 'application/json'},
							params: Ext.encode(favorite),
							failure: function(r) {
								ns.viewport.mask.show();
								alert(r.responseText);
							},
							success: function(r) {
								var id = r.getAllResponseHeaders().location.split('/').pop();

								ns.favorite = favorite;

								ns.store.reportTable.loadStore();

								//ns.viewport.interpretationButton.enable();

								window.destroy();
							}
						});
					}
				}
			});

			updateButton = Ext.create('Ext.button.Button', {
				text: NS.i18n.update,
				handler: function() {
					var name = nameTextfield.getValue(),
						reportTable;

					if (id && name) {
						Ext.Ajax.request({
							url: ns.init.contextPath + '/api/reportTables/' + id + '.json?viewClass=dimensional&links=false',
							method: 'GET',
							failure: function(r) {
								ns.viewport.mask.show();
								alert(r.responseText);
							},
							success: function(r) {
								reportTable = Ext.decode(r.responseText);
								reportTable.name = name;

								//tmp
								//delete reportTable.legendSet;

								Ext.Ajax.request({
									url: ns.init.contextPath + '/api/reportTables/' + reportTable.id,
									method: 'PUT',
									headers: {'Content-Type': 'application/json'},
									params: Ext.encode(reportTable),
									failure: function(r) {
										ns.viewport.mask.show();
										alert(r.responseText);
									},
									success: function(r) {
										ns.store.reportTable.loadStore();
										window.destroy();
									}
								});
							}
						});
					}
				}
			});

			cancelButton = Ext.create('Ext.button.Button', {
				text: NS.i18n.cancel,
				handler: function() {
					window.destroy();
				}
			});

			window = Ext.create('Ext.window.Window', {
				title: id ? 'Rename favorite' : 'Create new favorite',
				//iconCls: 'ns-window-title-icon-favorite',
				bodyStyle: 'padding:2px; background:#fff',
				resizable: false,
				modal: true,
				items: nameTextfield,
				destroyOnBlur: true,
				bbar: [
					cancelButton,
					'->',
					id ? updateButton : createButton
				],
				listeners: {
					show: function(w) {
						ns.util.window.setAnchorPosition(w, addButton);

						if (!w.hasDestroyBlurHandler) {
							ns.util.window.addDestroyOnBlurHandler(w);
						}

						ns.viewport.favoriteWindow.destroyOnBlur = false;
					},
					destroy: function() {
						ns.viewport.favoriteWindow.destroyOnBlur = true;
					}
				}
			});

			return window;
		};

		addButton = Ext.create('Ext.button.Button', {
			text: NS.i18n.add_new,
			width: 67,
			height: 26,
			style: 'border-radius: 1px;',
			menu: {},
			disabled: !Ext.isObject(ns.xLayout),
			handler: function() {
				nameWindow = new NameWindow(null, 'create');
				nameWindow.show();
			}
		});

		searchTextfield = Ext.create('Ext.form.field.Text', {
			width: windowCmpWidth - addButton.width - 11,
			height: 26,
			fieldStyle: 'padding-right: 0; padding-left: 6px; border-radius: 1px; border-color: #bbb; font-size:11px',
			emptyText: NS.i18n.search_for_favorites,
			enableKeyEvents: true,
			currentValue: '',
			listeners: {
				keyup: function() {
					if (this.getValue() !== this.currentValue) {
						this.currentValue = this.getValue();

						var value = this.getValue(),
							url = value ? ns.init.contextPath + '/api/reportTables/query/' + value + '.json?viewClass=sharing&links=false' : null,
							store = ns.store.reportTable;

						store.page = 1;
						store.loadStore(url);
					}
				}
			}
		});

		prevButton = Ext.create('Ext.button.Button', {
			text: NS.i18n.prev,
			handler: function() {
				var value = searchTextfield.getValue(),
					url = value ? ns.init.contextPath + '/api/reportTables/query/' + value + '.json?viewClass=sharing&links=false' : null,
					store = ns.store.reportTable;

				store.page = store.page <= 1 ? 1 : store.page - 1;
				store.loadStore(url);
			}
		});

		nextButton = Ext.create('Ext.button.Button', {
			text: NS.i18n.next,
			handler: function() {
				var value = searchTextfield.getValue(),
					url = value ? ns.init.contextPath + '/api/reportTables/query/' + value + '.json?viewClass=sharing&links=false' : null,
					store = ns.store.reportTable;

				store.page = store.page + 1;
				store.loadStore(url);
			}
		});

		info = Ext.create('Ext.form.Label', {
			cls: 'ns-label-info',
			width: 300,
			height: 22
		});

		grid = Ext.create('Ext.grid.Panel', {
			cls: 'ns-grid',
			scroll: false,
			hideHeaders: true,
			columns: [
				{
					dataIndex: 'name',
					sortable: false,
					width: windowCmpWidth - 88,
					renderer: function(value, metaData, record) {
						var fn = function() {
							var element = Ext.get(record.data.id);

							if (element) {
								element = element.parent('td');
								element.addClsOnOver('link');
								element.load = function() {
									favoriteWindow.hide();
									ns.engine.loadTable(record.data.id, ns, true, true);
								};
								element.dom.setAttribute('onclick', 'Ext.get(this).load();');
							}
						};

						Ext.defer(fn, 100);

						return '<div id="' + record.data.id + '">' + value + '</div>';
					}
				},
				{
					xtype: 'actioncolumn',
					sortable: false,
					width: 80,
					items: [
						{
							iconCls: 'ns-grid-row-icon-edit',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-edit' + (!record.data.access.update ? ' disabled' : '');
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex);

								if (record.data.access.update) {
									nameWindow = new NameWindow(record.data.id);
									nameWindow.show();
								}
							}
						},
						{
							iconCls: 'ns-grid-row-icon-overwrite',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-overwrite' + (!record.data.access.update ? ' disabled' : '');
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex),
									message,
									favorite;

								if (record.data.access.update) {
									message = NS.i18n.overwrite_favorite + '?\n\n' + record.data.name;
									favorite = getBody();

									if (favorite) {
										favorite.name = record.data.name;

										if (confirm(message)) {
											Ext.Ajax.request({
												url: ns.init.contextPath + '/api/reportTables/' + record.data.id,
												method: 'PUT',
												headers: {'Content-Type': 'application/json'},
												params: Ext.encode(favorite),
												success: function() {
													ns.favorite = favorite;
													ns.viewport.interpretationButton.enable();
													ns.store.reportTable.loadStore();
												}
											});
										}
									}
									else {
										alert(NS.i18n.please_create_a_table_first);
									}
								}
							}
						},
						{
							iconCls: 'ns-grid-row-icon-sharing',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-sharing' + (!record.data.access.manage ? ' disabled' : '');
							},
							handler: function(grid, rowIndex) {
								var record = this.up('grid').store.getAt(rowIndex);

								if (record.data.access.manage) {
									Ext.Ajax.request({
										url: ns.init.contextPath + '/api/sharing?type=reportTable&id=' + record.data.id,
										method: 'GET',
										failure: function(r) {
											ns.viewport.mask.hide();
											alert(r.responseText);
										},
										success: function(r) {
											var sharing = Ext.decode(r.responseText),
												window = NS.app.SharingWindow(sharing);
											window.show();
										}
									});
								}
							}
						},
						{
							iconCls: 'ns-grid-row-icon-delete',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-delete' + (!record.data.access['delete'] ? ' disabled' : '');
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex),
									message;

								if (record.data.access['delete']) {
									message = NS.i18n.delete_favorite + '?\n\n' + record.data.name;

									if (confirm(message)) {
										Ext.Ajax.request({
											url: ns.init.contextPath + '/api/reportTables/' + record.data.id,
											method: 'DELETE',
											success: function() {
												ns.store.reportTable.loadStore();
											}
										});
									}
								}
							}
						}
					]
				},
				{
					sortable: false,
					width: 6
				}
			],
			store: ns.store.reportTable,
			bbar: [
				info,
				'->',
				prevButton,
				nextButton
			],
			listeners: {
				added: function() {
					ns.viewport.favoriteGrid = this;
				},
				render: function() {
					var size = Math.floor((ns.viewport.centerRegion.getHeight() - 155) / ns.conf.layout.grid_row_height);
					this.store.pageSize = size;
					this.store.page = 1;
					this.store.loadStore();

					ns.store.reportTable.on('load', function() {
						if (this.isVisible()) {
							this.fireEvent('afterrender');
						}
					}, this);
				},
				afterrender: function() {
					var fn = function() {
						var editArray = Ext.query('.tooltip-favorite-edit'),
							overwriteArray = Ext.query('.tooltip-favorite-overwrite'),
							//dashboardArray = Ext.query('.tooltip-favorite-dashboard'),
							sharingArray = Ext.query('.tooltip-favorite-sharing'),
							deleteArray = Ext.query('.tooltip-favorite-delete'),
							el;

						for (var i = 0; i < editArray.length; i++) {
							var el = editArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: NS.i18n.rename,
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}

						for (var i = 0; i < overwriteArray.length; i++) {
							el = overwriteArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: NS.i18n.overwrite,
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}

						for (var i = 0; i < sharingArray.length; i++) {
							el = sharingArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: NS.i18n.share_with_other_people,
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}

						for (var i = 0; i < deleteArray.length; i++) {
							el = deleteArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: NS.i18n.delete_,
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}
					};

					Ext.defer(fn, 100);
				},
				itemmouseenter: function(grid, record, item) {
					this.currentItem = Ext.get(item);
					this.currentItem.removeCls('x-grid-row-over');
				},
				select: function() {
					this.currentItem.removeCls('x-grid-row-selected');
				},
				selectionchange: function() {
					this.currentItem.removeCls('x-grid-row-focused');
				}
			}
		});

		favoriteWindow = Ext.create('Ext.window.Window', {
			title: NS.i18n.manage_favorites,
			//iconCls: 'ns-window-title-icon-favorite',
			bodyStyle: 'padding:5px; background-color:#fff',
			resizable: false,
			modal: true,
			width: windowWidth,
			destroyOnBlur: true,
			items: [
				{
					xtype: 'panel',
					layout: 'hbox',
					bodyStyle: 'border:0 none',
					items: [
						addButton,
						{
							height: 24,
							width: 1,
							style: 'width:1px; margin-left:5px; margin-right:5px; margin-top:1px',
							bodyStyle: 'border-left: 1px solid #aaa'
						},
						searchTextfield
					]
				},
				grid
			],
			listeners: {
				show: function(w) {
					ns.util.window.setAnchorPosition(w, ns.viewport.favoriteButton);

					if (!w.hasDestroyOnBlurHandler) {
						ns.util.window.addDestroyOnBlurHandler(w);
					}
				}
			}
		});

		return favoriteWindow;
	};

	SharingWindow = function(sharing) {

		// Objects
		var UserGroupRow,

		// Functions
			getBody,

		// Components
			userGroupStore,
			userGroupField,
			userGroupButton,
			userGroupRowContainer,
			externalAccess,
			publicGroup,
			window;

		UserGroupRow = function(obj, isPublicAccess, disallowPublicAccess) {
			var getData,
				store,
				getItems,
				combo,
				getAccess,
				panel;

			getData = function() {
				var data = [
					{id: 'r-------', name: NS.i18n.can_view},
					{id: 'rw------', name: NS.i18n.can_edit_and_view}
				];

				if (isPublicAccess) {
					data.unshift({id: '-------', name: NS.i18n.none});
				}

				return data;
			}

			store = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				data: getData()
			});

			getItems = function() {
				var items = [];

				combo = Ext.create('Ext.form.field.ComboBox', {
					fieldLabel: isPublicAccess ? NS.i18n.public_access : obj.name,
					labelStyle: 'color:#333',
					cls: 'ns-combo',
					width: 380,
					labelWidth: 250,
					queryMode: 'local',
					valueField: 'id',
					displayField: 'name',
					labelSeparator: null,
					editable: false,
					disabled: !!disallowPublicAccess,
					value: obj.access || 'rw------',
					store: store
				});

				items.push(combo);

				if (!isPublicAccess) {
					items.push(Ext.create('Ext.Img', {
						src: 'images/grid-delete_16.png',
						style: 'margin-top:2px; margin-left:7px',
						overCls: 'pointer',
						width: 16,
						height: 16,
						listeners: {
							render: function(i) {
								i.getEl().on('click', function(e) {
									i.up('panel').destroy();
									window.doLayout();
								});
							}
						}
					}));
				}

				return items;
			};

			getAccess = function() {
				return {
					id: obj.id,
					name: obj.name,
					access: combo.getValue()
				};
			};

			panel = Ext.create('Ext.panel.Panel', {
				layout: 'column',
				bodyStyle: 'border:0 none',
				getAccess: getAccess,
				items: getItems()
			});

			return panel;
		};

		getBody = function() {
			var body = {
				object: {
					id: sharing.object.id,
					name: sharing.object.name,
					publicAccess: publicGroup.down('combobox').getValue(),
					externalAccess: externalAccess ? externalAccess.getValue() : false,
					user: {
						id: ns.init.user.id,
						name: ns.init.user.name
					}
				}
			};

			if (userGroupRowContainer.items.items.length > 1) {
				body.object.userGroupAccesses = [];
				for (var i = 1, item; i < userGroupRowContainer.items.items.length; i++) {
					item = userGroupRowContainer.items.items[i];
					body.object.userGroupAccesses.push(item.getAccess());
				}
			}

			return body;
		};

		// Initialize
		userGroupStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: ns.init.contextPath + '/api/sharing/search',
				reader: {
					type: 'json',
					root: 'userGroups'
				}
			}
		});

		userGroupField = Ext.create('Ext.form.field.ComboBox', {
			valueField: 'id',
			displayField: 'name',
			emptyText: NS.i18n.search_for_user_groups,
			queryParam: 'key',
			queryDelay: 200,
			minChars: 1,
			hideTrigger: true,
			fieldStyle: 'height:26px; padding-left:6px; border-radius:1px; font-size:11px',
			style: 'margin-bottom:5px',
			width: 380,
			store: userGroupStore,
			listeners: {
				beforeselect: function(cb) { // beforeselect instead of select, fires regardless of currently selected item
					userGroupButton.enable();
				},
				afterrender: function(cb) {
					cb.inputEl.on('keyup', function() {
						userGroupButton.disable();
					});
				}
			}
		});

		userGroupButton = Ext.create('Ext.button.Button', {
			text: '+',
			style: 'margin-left:2px; padding-right:4px; padding-left:4px; border-radius:1px',
			disabled: true,
			height: 26,
			handler: function(b) {
				userGroupRowContainer.add(UserGroupRow({
					id: userGroupField.getValue(),
					name: userGroupField.getRawValue(),
					access: 'r-------'
				}));

				userGroupField.clearValue();
				b.disable();
			}
		});

		userGroupRowContainer = Ext.create('Ext.container.Container', {
			bodyStyle: 'border:0 none'
		});

		if (sharing.meta.allowExternalAccess) {
			externalAccess = userGroupRowContainer.add({
				xtype: 'checkbox',
				fieldLabel: NS.i18n.allow_external_access,
				labelSeparator: '',
				labelWidth: 250,
				checked: !!sharing.object.externalAccess
			});
		}

		publicGroup = userGroupRowContainer.add(UserGroupRow({
			id: sharing.object.id,
			name: sharing.object.name,
			access: sharing.object.publicAccess
		}, true, !sharing.meta.allowPublicAccess));

		if (Ext.isArray(sharing.object.userGroupAccesses)) {
			for (var i = 0, userGroupRow; i < sharing.object.userGroupAccesses.length; i++) {
				userGroupRow = UserGroupRow(sharing.object.userGroupAccesses[i]);
				userGroupRowContainer.add(userGroupRow);
			}
		}

		window = Ext.create('Ext.window.Window', {
			title: NS.i18n.sharing_settings,
			bodyStyle: 'padding:6px 6px 0px; background-color:#fff',
			resizable: false,
			modal: true,
			destroyOnBlur: true,
			items: [
				{
					html: sharing.object.name,
					bodyStyle: 'border:0 none; font-weight:bold; color:#333',
					style: 'margin-bottom:8px'
				},
				{
					xtype: 'container',
					layout: 'column',
					bodyStyle: 'border:0 none',
					items: [
						userGroupField,
						userGroupButton
					]
				},
				userGroupRowContainer
			],
			bbar: [
				'->',
				{
					text: NS.i18n.save,
					handler: function() {
						Ext.Ajax.request({
							url: ns.init.contextPath + '/api/sharing?type=reportTable&id=' + sharing.object.id,
							method: 'POST',
							headers: {
								'Content-Type': 'application/json'
							},
							params: Ext.encode(getBody())
						});

						window.destroy();
					}
				}
			],
			listeners: {
				show: function(w) {
					var pos = ns.viewport.favoriteWindow.getPosition();
					w.setPosition(pos[0] + 5, pos[1] + 5);

					if (!w.hasDestroyOnBlurHandler) {
						ns.util.window.addDestroyOnBlurHandler(w);
					}

					ns.viewport.favoriteWindow.destroyOnBlur = false;
				},
				destroy: function() {
					ns.viewport.favoriteWindow.destroyOnBlur = true;
				}
			}
		});

		return window;
	};

	InterpretationWindow = function() {
		var textArea,
			linkPanel,
			shareButton,
			window;

		if (Ext.isObject(ns.favorite) && Ext.isString(ns.favorite.id)) {
			textArea = Ext.create('Ext.form.field.TextArea', {
				cls: 'ns-textarea',
				height: 130,
				fieldStyle: 'padding-left: 4px; padding-top: 3px',
				emptyText: NS.i18n.write_your_interpretation,
				enableKeyEvents: true,
				listeners: {
					keyup: function() {
						shareButton.xable();
					}
				}
			});

			linkPanel = Ext.create('Ext.panel.Panel', {
				html: function() {
					var reportTableUrl = ns.init.contextPath + '/dhis-web-pivot/app/index.html?id=' + ns.favorite.id,
						apiUrl = ns.init.contextPath + '/api/reportTables/' + ns.favorite.id + '/data.html',
						html = '';

					html += '<div><b>Pivot link: </b><span class="user-select"><a href="' + reportTableUrl + '" target="_blank">' + reportTableUrl + '</a></span></div>';
					html += '<div style="padding-top:3px"><b>API link: </b><span class="user-select"><a href="' + apiUrl + '" target="_blank">' + apiUrl + '</a></span></div>';
					return html;
				}(),
				style: 'padding-top: 8px; padding-bottom: 5px',
				bodyStyle: 'border: 0 none'
			});

			shareButton = Ext.create('Ext.button.Button', {
				text: NS.i18n.share,
				disabled: true,
				xable: function() {
					this.setDisabled(!textArea.getValue());
				},
				handler: function() {
					if (textArea.getValue()) {
						Ext.Ajax.request({
							url: ns.init.contextPath + '/api/interpretations/reportTable/' + ns.favorite.id,
							method: 'POST',
							params: textArea.getValue(),
							headers: {'Content-Type': 'text/html'},
							success: function() {
								textArea.reset();
								ns.viewport.interpretationButton.disable();
								window.hide();
								//NS.util.notification.interpretation(NS.i18n.interpretation_was_shared + '.');
							}
						});
					}
				}
			});

			window = Ext.create('Ext.window.Window', {
				title: ns.favorite.name,
				layout: 'fit',
				//iconCls: 'ns-window-title-interpretation',
				width: 500,
				bodyStyle: 'padding:5px 5px 3px; background-color:#fff',
				resizable: true,
				destroyOnBlur: true,
				modal: true,
				items: [
					textArea,
					linkPanel
				],
				bbar: {
					cls: 'ns-toolbar-bbar',
					defaults: {
						height: 24
					},
					items: [
						'->',
						shareButton
					]
				},
				listeners: {
					show: function(w) {
						ns.util.window.setAnchorPosition(w, ns.viewport.interpretationButton);

						document.body.oncontextmenu = true;

						if (!w.hasDestroyOnBlurHandler) {
							ns.util.window.addDestroyOnBlurHandler(w);
						}
					},
					hide: function() {
						document.body.oncontextmenu = function(){return false;};
					},
					destroy: function() {
						ns.viewport.interpretationWindow = null;
					}
				}
			});

			return window;
		}

		return;
	};

	// core
	extendCore = function(core) {
        var init = core.init,
            conf = core.conf,
            util = {},
            api = core.api,
            support = core.support,
            service = core.service,
            web = core.web,
            store = {},
            dimConf = conf.finals.dimension;

		//tmp
		ns.util = util;

		// util
		(function() {

			util.checkbox = {
				setAllFalse: function() {
					var a = cmp.dimension.relativePeriod.checkbox;
					for (var i = 0; i < a.length; i++) {
						a[i].setValue(false);
					}
				},
				isAllFalse: function() {
					var a = cmp.dimension.relativePeriod.checkbox;
					for (var i = 0; i < a.length; i++) {
						if (a[i].getValue()) {
							return false;
						}
					}
					return true;
				}
			};

			util.multiselect = {
				select: function(a, s) {
					var selected = a.getValue();
					if (selected.length) {
						var array = [];
						Ext.Array.each(selected, function(item) {
							array.push({id: item, name: a.store.getAt(a.store.findExact('id', item)).data.name});
						});
						s.store.add(array);
					}
					this.filterAvailable(a, s);
				},
				selectAll: function(a, s, doReverse) {
					var array = [];
					a.store.each( function(r) {
						array.push({id: r.data.id, name: r.data.name});
					});
					if (doReverse) {
						array.reverse();
					}
					s.store.add(array);
					this.filterAvailable(a, s);
				},
				unselect: function(a, s) {
					var selected = s.getValue();
					if (selected.length) {
						Ext.Array.each(selected, function(item) {
							s.store.remove(s.store.getAt(s.store.findExact('id', item)));
						});
						this.filterAvailable(a, s);
					}
				},
				unselectAll: function(a, s) {
					s.store.removeAll();
					a.store.clearFilter();
					this.filterAvailable(a, s);
				},
				filterAvailable: function(a, s) {
					a.store.filterBy( function(r) {
						var keep = true;
						s.store.each( function(r2) {
							if (r.data.id == r2.data.id) {
								keep = false;
							}

						});
						return keep;
					});
					a.store.sortStore();
				},
				setHeight: function(ms, panel, fill) {
					for (var i = 0; i < ms.length; i++) {
						ms[i].setHeight(panel.getHeight() - fill);
					}
				}
			};

			util.url = {
				getUrlParam: function(s) {
					var output = '';
					var href = window.location.href;
					if (href.indexOf('?') > -1 ) {
						var query = href.substr(href.indexOf('?') + 1);
						var query = query.split('&');
						for (var i = 0; i < query.length; i++) {
							if (query[i].indexOf('=') > -1) {
								var a = query[i].split('=');
								if (a[0].toLowerCase() === s) {
									output = a[1];
									break;
								}
							}
						}
					}
					return unescape(output);
				}
			};

			util.window = util.window || {};

			util.window.setAnchorPosition = function(w, target) {
				var vpw = ns.viewport.getWidth(),
					targetx = target ? target.getPosition()[0] : 4,
					winw = w.getWidth(),
					y = target ? target.getPosition()[1] + target.getHeight() + 4 : 33;

				if ((targetx + winw) > vpw) {
					w.setPosition((vpw - winw - 2), y);
				}
				else {
					w.setPosition(targetx, y);
				}
			};

			util.window.addHideOnBlurHandler = function(w) {
				var el = Ext.get(Ext.query('.x-mask')[0]);

				el.on('click', function() {
					if (w.hideOnBlur) {
						w.hide();
					}
				});

				w.hasHideOnBlurHandler = true;
			};

			util.window.addDestroyOnBlurHandler = function(w) {
				var el = Ext.get(Ext.query('.x-mask')[0]);

				el.on('click', function() {
					if (w.destroyOnBlur) {
						w.destroy();
					}
				});

				w.hasDestroyOnBlurHandler = true;
			};

			util.message = {
				alert: function(message) {
					alert(message);
				}
			}
		}());

        // init
        (function() {

			// root nodes
			for (var i = 0; i < init.rootNodes.length; i++) {
				init.rootNodes[i].path = '/' + conf.finals.root.id + '/' + init.rootNodes[i].id;
			}

			// viewport afterrender
			init.afterRender = function() {

				// Resize event handler
				ns.viewport.westRegion.on('resize', function() {
					var panel = util.dimension.panel.getExpandedPanel();

					if (panel) {
						panel.onExpand();
					}
				});

				// Left gui
				var viewportHeight = ns.viewport.westRegion.getHeight(),
					numberOfTabs = init.dimensions.length + 5,
					tabHeight = 28,
					minPeriodHeight = 380;

				if (viewportHeight > numberOfTabs * tabHeight + minPeriodHeight) {
					if (!Ext.isIE) {
						ns.viewport.accordion.setAutoScroll(false);
						ns.viewport.westRegion.setWidth(conf.layout.west_width);
						ns.viewport.accordion.doLayout();
					}
				}
				else {
					ns.viewport.westRegion.hasScrollbar = true;
				}

                // Expand first panel
				cmp.dimension.panels[0].expand();

                // Look for url params
				var id = util.url.getUrlParam('id'),
					session = util.url.getUrlParam('s'),
					layout;

				if (id) {
					web.loadTable(id, ns, true, true);
				}
				else if (Ext.isString(session) && NS.isSessionStorage && Ext.isObject(JSON.parse(sessionStorage.getItem('dhis2'))) && session in JSON.parse(sessionStorage.getItem('dhis2'))) {
					layout = api.layout.Layout(JSON.parse(sessionStorage.getItem('dhis2'))[session]);

					if (layout) {
						web.createTable(layout, ns, true);
					}
				}

				// Fade in
				Ext.defer( function() {
					Ext.getBody().fadeIn({
						duration: 300
					});
				}, 400 );
			};
		}());

		// web
		(function() {

			// storage
			web.storage = web.storage || {};

				// internal
			web.storage.internal = web.storage.internal || {};

			web.storage.internal.add = function(store, storage, parent, records) {
				if (!Ext.isObject(store)) {
					console.log('support.storeage.add: store is not an object');
					return null;
				}

				storage = storage || store.storage;
				parent = parent || store.parent;

				if (!Ext.isObject(storage)) {
					console.log('support.storeage.add: storage is not an object');
					return null;
				}

				store.each( function(r) {
					if (storage[r.data.id]) {
						storage[r.data.id] = {id: r.data.id, name: r.data.name, parent: parent};
					}
				});

				if (support.prototype.array.getLength(records, true)) {
					Ext.Array.each(records, function(r) {
						if (storage[r.data.id]) {
							storage[r.data.id] = {id: r.data.id, name: r.data.name, parent: parent};
						}
					});
				}
			};

			web.storage.internal.load = function(store, storage, parent, records) {
				var a = [];

				if (!Ext.isObject(store)) {
					console.log('support.storeage.load: store is not an object');
					return null;
				}

				storage = storage || store.storage;
				parent = parent || store.parent;

				store.removeAll();

				for (var key in storage) {
					var record = storage[key];

					if (storage.hasOwnProperty(key) && record.parent === parent) {
						a.push(record);
					}
				}

				if (support.prototype.array.getLength(records)) {
					a = a.concat(records);
				}

				store.add(a);
				store.sort('name', 'ASC');
			};

				// session
			web.storage.session = web.storage.session || {};

			web.storage.session.set = function(layout, session, url) {
				if (NS.isSessionStorage) {
					var dhis2 = JSON.parse(sessionStorage.getItem('dhis2')) || {};
					dhis2[session] = layout;
					sessionStorage.setItem('dhis2', JSON.stringify(dhis2));

					if (Ext.isString(url)) {
						window.location.href = url;
					}
				}
			};

			// mouse events
			web.events = web.events || {};

			web.events.setMouseHandlers = function(layout, response, uuidDimUuidsMap, uuidObjectMap) {
				var valueEl;

				for (var key in uuidDimUuidsMap) {
					if (uuidDimUuidsMap.hasOwnProperty(key)) {
						valueEl = Ext.get(key);

						if (parseFloat(valueEl.dom.textContent)) {
							valueEl.dom.onMouseClick = web.events.onMouseClick;
							valueEl.dom.layout = layout;
							valueEl.dom.response = response;
							valueEl.dom.uuidDimUuidsMap = uuidDimUuidsMap;
							valueEl.dom.uuidObjectMap = uuidObjectMap;
							valueEl.dom.setAttribute('onclick', 'this.onMouseClick(this.layout, this.response, this.uuidDimUuidsMap, this.uuidObjectMap, this.id);');
						}
					}
				}
			};

			web.events.onMouseClick = function(layout, response, uuidDimUuidsMap, uuidObjectMap, uuid) {
				var uuids = uuidDimUuidsMap[uuid],
					layoutConfig = Ext.clone(layout),
					parentGraphMap = ns.app.viewport.treePanel.getParentGraphMap(),
					objects = [],
					menu;

				// modify layout dimension items based on uuid objects

				// get objects
				for (var i = 0; i < uuids.length; i++) {
					objects.push(uuidObjectMap[uuids[i]]);
				}

				// clear layoutConfig dimension items
				for (var i = 0, a = Ext.Array.clean([].concat(layoutConfig.columns || [], layoutConfig.rows || [])); i < a.length; i++) {
					a[i].items = [];
				}

				// add new items
				for (var i = 0, obj, axis; i < objects.length; i++) {
					obj = objects[i];

					axis = obj.axis === 'col' ? layoutConfig.columns || [] : layoutConfig.rows || [];

					if (axis.length) {
						axis[obj.dim].items.push({
							id: obj.id,
							name: response.metaData.names[obj.id]
						});
					}
				}

				// parent graph map
				layoutConfig.parentGraphMap = {};

				for (var i = 0, id; i < objects.length; i++) {
					id = objects[i].id;

					if (parentGraphMap.hasOwnProperty(id)) {
						layoutConfig.parentGraphMap[id] = parentGraphMap[id];
					}
				}

				// menu
				menu = Ext.create('Ext.menu.Menu', {
					shadow: true,
					showSeparator: false,
					items: [
						{
							text: 'Open selection as chart' + '&nbsp;&nbsp;', //i18n
							iconCls: 'ns-button-icon-chart',
							param: 'chart',
							handler: function() {
								web.storage.session.set('analytical', layoutConfig, init.contextPath + '/dhis-web-visualizer/app/index.html?s=analytical');
							},
							listeners: {
								render: function() {
									this.getEl().on('mouseover', function() {
										web.events.onMouseHover(uuidDimUuidsMap, uuid, 'mouseover', 'chart');
									});

									this.getEl().on('mouseout', function() {
										web.events.onMouseHover(uuidDimUuidsMap, uuid, 'mouseout', 'chart');
									});
								}
							}
						},
						{
							text: 'Open selection as map' + '&nbsp;&nbsp;', //i18n
							iconCls: 'ns-button-icon-map',
							param: 'map',
							disabled: true,
							handler: function() {
								web.storage.session.set('analytical', layoutConfig, init.contextPath + '/dhis-web-mapping/app/index.html?s=analytical');
							},
							listeners: {
								render: function() {
									this.getEl().on('mouseover', function() {
										web.events.onMouseHover(uuidDimUuidsMap, uuid, 'mouseover', 'map');
									});

									this.getEl().on('mouseout', function() {
										web.events.onMouseHover(uuidDimUuidsMap, uuid, 'mouseout', 'map');
									});
								}
							}
						}
					]
				});

				menu.showAt(function() {
					var el = Ext.get(uuid),
						xy = el.getXY();

					xy[0] += el.getWidth() - 5;
					xy[1] += el.getHeight() - 5;

					return xy;
				}());
			};

			web.events.onMouseHover = function(uuidDimUuidsMap, uuid, event, param) {
				var dimUuids;

				if (param === 'chart') {
					if (Ext.isString(uuid) && Ext.isArray(uuidDimUuidsMap[uuid])) {
						dimUuids = uuidDimUuidsMap[uuid];

						for (var i = 0, el; i < dimUuids.length; i++) {
							el = Ext.get(dimUuids[i]);

							if (el) {
								if (event === 'mouseover') {
									el.addCls('highlighted');
								}
								else if (event === 'mouseout') {
									el.removeCls('highlighted');
								}
							}
						}
					}
				}
			};

			// pivot
			web.pivot = web.pivot || {};

			web.pivot.loadTable = function(id) {
				if (!Ext.isString(id)) {
					alert('Invalid report table id');
					return;
				}

				Ext.Ajax.request({
					url: init.contextPath + '/api/reportTables/.json?viewClass=dimensional&links=false',
					failure: function(r) {
						web.mask.hide(ns.app.centerRegion);
						alert(r.responseText);
					},
					success: function(r) {,
						var layoutConfig = Ext.decode(r.responseText),
							layout = api.layout.Layout(layoutConfig);

						if (layout) {
							//ns.favorite = Ext.clone(layout);
							//ns.favorite.id = layoutConfig.id;
							//ns.favorite.name = layoutConfig.name;

							web.pivot.createTable(layout, true);
						}
					};
				});

			web.pivot.createTable = function(layout, isUpdateGui) {



			};
		}());

		// store
		(function() {
			store.indicatorAvailable = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				proxy: {
					type: 'ajax',
					reader: {
						type: 'json',
						root: 'indicators'
					}
				},
				storage: {},
				parent: null,
				sortStore: function() {
					this.sort('name', 'ASC');
				},
				listeners: {
					load: function(s) {
						support.storage.add(s);
						util.multiselect.filterAvailable({store: s}, {store: store.indicatorSelected});
					}
				}
			});

			store.indicatorSelected = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				data: []
			});

			store.dataElementAvailable = Ext.create('Ext.data.Store', {
				fields: ['id', 'name', 'dataElementId', 'onsionComboId', 'operandName'],
				proxy: {
					type: 'ajax',
					reader: {
						type: 'json',
						root: 'dataElements'
					}
				},
				storage: {},
				sortStore: function() {
					this.sort('name', 'ASC');
				},
				setTotalsProxy: function(uid) {
					var path;

					if (Ext.isString(uid)) {
						path = 'dataElementGroups/' + uid + '.json?links=false&paging=false';
					}
					else if (uid === 0) {
						path = 'dataElements.json?paging=false&links=false';
					}

					if (!path) {
						alert('Available data elements: invalid id');
						return;
					}

					this.setProxy({
						type: 'ajax',
						url: init.contextPath + '/api' + path,
						reader: {
							type: 'json',
							root: 'dataElements'
						}
					});

					this.load({
						scope: this,
						callback: function() {
							util.multiselect.filterAvailable({store: this}, {store: store.dataElementSelected});
						}
					});
				},
				setDetailsProxy: function(uid) {
					if (Ext.isString(uid)) {
						this.setProxy({
							type: 'ajax',
							url: init.contextPath + '/dhis-web-commons-ajax-json/getOperands.action?uid=' + uid,
							reader: {
								type: 'json',
								root: 'operands'
							}
						});

						this.load({
							scope: this,
							callback: function() {
								this.each(function(r) {
									r.set('id', r.data.dataElementId + '-' + r.data.onsionComboId);
									r.set('name', r.data.operandName);
								});

								util.multiselect.filterAvailable({store: this}, {store: store.dataElementSelected});
							}
						});
					}
					else {
						alert('Invalid parameter');
					}
				},
				listeners: {
					load: function(s) {
						support.storage.add(s);
						util.multiselect.filterAvailable({store: s}, {store: store.dataElementSelected});
					}
				}
			});

			store.dataElementSelected = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				data: []
			});

			store.dataSetAvailable = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				proxy: {
					type: 'ajax',
					url: init.contextPath + '/api/dataSets.json?paging=false&links=false',
					reader: {
						type: 'json',
						root: 'dataSets'
					}
				},
				storage: {},
				sortStore: function() {
					this.sort('name', 'ASC');
				},
				isLoaded: false,
				listeners: {
					load: function(s) {
						this.isLoaded = true;

						support.storage.add(s);
						util.multiselect.filterAvailable({store: s}, {store: store.dataSetSelected});
					}
				}
			});

			store.dataSetSelected = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				data: []
			});

			store.periodType = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				data: conf.period.periodTypes
			});

			store.fixedPeriodAvailable = Ext.create('Ext.data.Store', {
				fields: ['id', 'name', 'index'],
				data: [],
				setIndex: function(periods) {
					for (var i = 0; i < periods.length; i++) {
						periods[i].index = i;
					}
				},
				sortStore: function() {
					this.sort('index', 'ASC');
				}
			});

			store.fixedPeriodSelected = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				data: []
			});

			store.reportTable = Ext.create('Ext.data.Store', {
				fields: ['id', 'name', 'lastUpdated', 'access'],
				proxy: {
					type: 'ajax',
					reader: {
						type: 'json',
						root: 'reportTables'
					}
				},
				isLoaded: false,
				pageSize: 10,
				page: 1,
				defaultUrl: init.contextPath + '/api/reportTables.json?viewClass=sharing&links=false',
				loadStore: function(url) {
					this.proxy.url = url || this.defaultUrl;

					this.load({
						params: {
							pageSize: this.pageSize,
							page: this.page
						}
					});
				},
				loadFn: function(fn) {
					if (this.isLoaded) {
						fn.call();
					}
					else {
						this.load(fn);
					}
				},
				listeners: {
					load: function(s) {
						if (!this.isLoaded) {
							this.isLoaded = true;
						}

						this.sort('name', 'ASC');
					}
				}
			});

			store.organisationUnitGroup = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				proxy: {
					type: 'ajax',
					url: init.contextPath + '/api/organisationUnitGroups.json?paging=false&links=false',
					reader: {
						type: 'json',
						root: 'organisationUnitGroups'
					}
				}
			});

			store.legendSet = Ext.create('Ext.data.Store', {
				fields: ['id', 'name', 'index'],
				data: function() {
					var data = init.legendSets;
					data.unshift({id: 0, name: NS.i18n.none, index: -1});
					return data;
				}(),
				sorters: [
					{property: 'index', direction: 'ASC'},
					{property: 'name', direction: 'ASC'}
				]
			});

			ns.store = store;
		}());

		// engine
		(function() {
			engine.getLayoutConfig = function() {
				var panels = ns.cmp.dimension.panels,
					columnDimNames = ns.viewport.colStore.getDimensionNames(),
					rowDimNames = ns.viewport.rowStore.getDimensionNames(),
					filterDimNames = ns.viewport.filterStore.getDimensionNames(),
					config = ns.viewport.optionsWindow.getOptions(),
					dx = dimConf.data.dimensionName,
					co = dimConf.category.dimensionName,
					nameDimArrayMap = {};

				config.columns = [];
				config.rows = [];
				config.filters = [];

				// Panel data
				for (var i = 0, dim, dimName; i < panels.length; i++) {
					dim = panels[i].getDimension();

					if (dim) {
						nameDimArrayMap[dim.dimension] = [dim];
					}
				}

				nameDimArrayMap[dx] = Ext.Array.clean([].concat(
					nameDimArrayMap[dimConf.indicator.objectName],
					nameDimArrayMap[dimConf.dataElement.objectName],
					nameDimArrayMap[dimConf.operand.objectName],
					nameDimArrayMap[dimConf.dataSet.objectName]
				));

				// Columns, rows, filters
				for (var i = 0, nameArrays = [columnDimNames, rowDimNames, filterDimNames], axes = [config.columns, config.rows, config.filters], dimNames; i < nameArrays.length; i++) {
					dimNames = nameArrays[i];

					for (var j = 0, dimName, dim; j < dimNames.length; j++) {
						dimName = dimNames[j];

						if (dimName === co) {
							axes[i].push({
								dimension: co,
								items: []
							});
						}
						else if (dimName === dx && nameDimArrayMap.hasOwnProperty(dimName) && nameDimArrayMap[dimName]) {
							for (var k = 0; k < nameDimArrayMap[dx].length; k++) {
								axes[i].push(Ext.clone(nameDimArrayMap[dx][k]));
							}
						}
						else if (nameDimArrayMap.hasOwnProperty(dimName) && nameDimArrayMap[dimName]) {
							for (var k = 0; k < nameDimArrayMap[dimName].length; k++) {
								axes[i].push(Ext.clone(nameDimArrayMap[dimName][k]));
							}
						}
					}
				}

				return config;
			};
		}());
	};

	// viewport
	createViewport = function() {
        var dimConf = ns.conf.finals.dimension,

			indicatorAvailable,
			indicatorSelected,
			indicator,
			dataElementAvailable,
			dataElementSelected,
			dataElement,
			dataSetAvailable,
			dataSetSelected,
			dataSet,
			rewind,
			relativePeriod,
			fixedPeriodAvailable,
			fixedPeriodSelected,
			period,
			treePanel,
			userOrganisationUnit,
			userOrganisationUnitChildren,
			userOrganisationUnitGrandChildren,
			userOrganisationUnitPanel,
			organisationUnitLevel,
			tool,
			toolPanel,
			organisationUnit,
			dimensionIdAvailableStoreMap = {},
			dimensionIdSelectedStoreMap = {},
			getGroupSetPanels,
			update,

			layoutButton,
			optionsButton,
			favoriteButton,
			openTableLayoutTab,
			downloadButton,

			accordionBody,
			accordion,
			westRegion,
			centerRegion,

			setGui,

			viewport;

		// data
		indicatorAvailable = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-left',
			width: (ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			store: ns.store.indicatorAvailable,
			tbar: [
				{
					xtype: 'label',
					text: NS.i18n.available,
					cls: 'ns-toolbar-multiselect-left-label'
				},
				'->',
				{
					xtype: 'button',
					icon: 'images/arrowright.png',
					width: 22,
					handler: function() {
						ns.util.multiselect.select(indicatorAvailable, indicatorSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowrightdouble.png',
					width: 22,
					handler: function() {
						ns.util.multiselect.selectAll(indicatorAvailable, indicatorSelected);
					}
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						ns.util.multiselect.select(this, indicatorSelected);
					}, this);
				}
			}
		});

		indicatorSelected = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-right',
			width: (ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			ddReorder: true,
			store: ns.store.indicatorSelected,
			tbar: [
				{
					xtype: 'button',
					icon: 'images/arrowleftdouble.png',
					width: 22,
					handler: function() {
						ns.util.multiselect.unselectAll(indicatorAvailable, indicatorSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowleft.png',
					width: 22,
					handler: function() {
						ns.util.multiselect.unselect(indicatorAvailable, indicatorSelected);
					}
				},
				'->',
				{
					xtype: 'label',
					text: NS.i18n.selected,
					cls: 'ns-toolbar-multiselect-right-label'
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						ns.util.multiselect.unselect(indicatorAvailable, this);
					}, this);
				}
			}
		});

		indicator = {
			xtype: 'panel',
			title: '<div class="ns-panel-title-data">' + NS.i18n.indicators + '</div>',
			hideCollapseTool: true,
			getDimension: function() {
				var config = {
					dimension: ns.conf.finals.dimension.indicator.objectName,
					items: []
				};

				ns.store.indicatorSelected.each( function(r) {
					config.items.push({
						id: r.data.id,
						name: r.data.name
					});
				});

				return config.items.length ? config : null;
			},
			onExpand: function() {
				var h = westRegion.hasScrollbar ?
					ns.conf.layout.west_scrollbarheight_accordion_indicator : ns.conf.layout.west_maxheight_accordion_indicator;
				accordion.setThisHeight(h);
				ns.util.multiselect.setHeight(
					[indicatorAvailable, indicatorSelected],
					this,
					ns.conf.layout.west_fill_accordion_indicator
				);
			},
			items: [
				{
					xtype: 'combobox',
					cls: 'ns-combo',
					style: 'margin-bottom:2px; margin-top:0px',
					width: ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding,
					valueField: 'id',
					displayField: 'name',
					emptyText: NS.i18n.select_indicator_group,
					editable: false,
					store: {
						xtype: 'store',
						fields: ['id', 'name', 'index'],
						proxy: {
							type: 'ajax',
							url: ns.init.contextPath + '/api/indicatorGroups.json?paging=false&links=false',
							reader: {
								type: 'json',
								root: 'indicatorGroups'
							}
						},
						listeners: {
							load: function(s) {
								s.add({
									id: 0,
									name: NS.i18n.all_indicator_groups,
									index: -1
								});
								s.sort([
									{
										property: 'index',
										direction: 'ASC'
									},
									{
										property: 'name',
										direction: 'ASC'
									}
								]);
							}
						}
					},
					listeners: {
						select: function(cb) {
							var store = ns.store.indicatorAvailable,
								id = cb.getValue();

							store.parent = id;

							if (ns.support.prototype.object.hasObject(store.storage, 'parent', id)) {
								ns.support.store.load(store);
								ns.util.multiselect.filterAvailable(indicatorAvailable, indicatorSelected);
							}
							else {
								if (id === 0) {
									store.proxy.url = ns.init.contextPath + '/api/indicators.json?paging=false&links=false';
									store.load();
								}
								else {
									store.proxy.url = ns.init.contextPath + '/api/indicatorGroups/' + id + '.json';
									store.load();
								}
							}
						}
					}
				},
				{
					xtype: 'panel',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						indicatorAvailable,
						indicatorSelected
					]
				}
			],
			listeners: {
				added: function() {
					ns.app.accordion.panels.push(this);
				},
				expand: function(p) {
					p.onExpand();
				}
			}
		};

		dataElementAvailable = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-left',
			width: (ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			store: ns.store.dataElementAvailable,
			tbar: [
				{
					xtype: 'label',
					text: NS.i18n.available,
					cls: 'ns-toolbar-multiselect-left-label'
				},
				'->',
				{
					xtype: 'button',
					icon: 'images/arrowright.png',
					width: 22,
					handler: function() {
						ns.util.multiselect.select(dataElementAvailable, dataElementSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowrightdouble.png',
					width: 22,
					handler: function() {
						ns.util.multiselect.selectAll(dataElementAvailable, dataElementSelected);
					}
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						ns.util.multiselect.select(this, dataElementSelected);
					}, this);
				}
			}
		});

		dataElementSelected = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-right',
			width: (ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			ddReorder: true,
			store: ns.store.dataElementSelected,
			tbar: [
				{
					xtype: 'button',
					icon: 'images/arrowleftdouble.png',
					width: 22,
					handler: function() {
						ns.util.multiselect.unselectAll(dataElementAvailable, dataElementSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowleft.png',
					width: 22,
					handler: function() {
						ns.util.multiselect.unselect(dataElementAvailable, dataElementSelected);
					}
				},
				'->',
				{
					xtype: 'label',
					text: NS.i18n.selected,
					cls: 'ns-toolbar-multiselect-right-label'
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						ns.util.multiselect.unselect(dataElementAvailable, this);
					}, this);
				}
			}
		});

		dataElementGroupStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'index'],
			proxy: {
				type: 'ajax',
				url: ns.init.contextPath + '/api/dataElementGroups.json?paging=false&links=false',
				reader: {
					type: 'json',
					root: 'dataElementGroups'
				}
			},
			listeners: {
				load: function(s) {
					if (dataElementDetailLevel.getValue() === ns.conf.finals.dimension.dataElement.objectName) {
						s.add({
							id: 0,
							name: '[ ' + NS.i18n.all_data_element_groups + ' ]',
							index: -1
						});
					}

					s.sort([
						{property: 'index', direction: 'ASC'},
						{property: 'name', direction: 'ASC'}
					]);
				}
			}
		});

		dataElementGroupComboBox = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			style: 'margin:0 2px 2px 0',
			width: ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding - 90,
			valueField: 'id',
			displayField: 'name',
			emptyText: NS.i18n.select_data_element_group,
			editable: false,
			store: dataElementGroupStore,
			loadAvailable: function() {
				var store = ns.store.dataElementAvailable,
					detailLevel = dataElementDetailLevel.getValue(),
					value = this.getValue();

				if (value !== null) {
					if (detailLevel === ns.conf.finals.dimension.dataElement.objectName) {
						store.setTotalsProxy(value);
					}
					else {
						store.setDetailsProxy(value);
					}
				}
			},
			listeners: {
				select: function(cb) {
					cb.loadAvailable();
				}
			}
		});

		dataElementDetailLevel = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			style: 'margin-bottom:2px',
			baseBodyCls: 'small',
			queryMode: 'local',
			editable: false,
			valueField: 'id',
			displayField: 'text',
			width: 90 - 2,
			value: ns.conf.finals.dimension.dataElement.objectName,
			store: {
				fields: ['id', 'text'],
				data: [
					{id: ns.conf.finals.dimension.dataElement.objectName, text: NS.i18n.totals},
					{id: ns.conf.finals.dimension.operand.objectName, text: NS.i18n.details}
				]
			},
			listeners: {
				select: function(cb) {
					var record = dataElementGroupStore.getById(0);

					if (cb.getValue() === ns.conf.finals.dimension.operand.objectName && record) {
						dataElementGroupStore.remove(record);
					}

					if (cb.getValue() === ns.conf.finals.dimension.dataElement.objectName && !record) {
						dataElementGroupStore.insert(0, {
							id: 0,
							name: '[ ' + NS.i18n.all_data_element_groups + ' ]',
							index: -1
						});
					}

					dataElementGroupComboBox.loadAvailable();
					ns.store.dataElementSelected.removeAll();
				}
			}
		});

		dataElement = {
			xtype: 'panel',
			title: '<div class="ns-panel-title-data">' + NS.i18n.data_elements + '</div>',
			hideCollapseTool: true,
			getDimension: function() {
				var config = {
					dimension: dataElementDetailLevel.getValue(),
					items: []
				};

				ns.store.dataElementSelected.each( function(r) {
					config.items.push({
						id: r.data.id,
						name: r.data.name
					});
				});

				return config.items.length ? config : null;
			},
			onExpand: function() {
				var h = ns.viewport.westRegion.hasScrollbar ?
					ns.conf.layout.west_scrollbarheight_accordion_dataelement : ns.conf.layout.west_maxheight_accordion_dataelement;
				ns.util.dimension.panel.setHeight(h);
				ns.util.multiselect.setHeight(
					[dataElementAvailable, dataElementSelected],
					this,
					ns.conf.layout.west_fill_accordion_indicator
				);
			},
			items: [
				{
					xtype: 'container',
					layout: 'column',
					items: [
						dataElementGroupComboBox,
						dataElementDetailLevel
					]
				},
				{
					xtype: 'panel',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						dataElementAvailable,
						dataElementSelected
					]
				}
			],
			listeners: {
				added: function() {
					ns.app.accordion.panels.push(this);
				},
				expand: function(p) {
					p.onExpand();
				}
			}
		};

		dataSetAvailable = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-left',
			width: (ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			store: ns.store.dataSetAvailable,
			tbar: [
				{
					xtype: 'label',
					text: NS.i18n.available,
					cls: 'ns-toolbar-multiselect-left-label'
				},
				'->',
				{
					xtype: 'button',
					icon: 'images/arrowright.png',
					width: 22,
					handler: function() {
						ns.util.multiselect.select(dataSetAvailable, dataSetSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowrightdouble.png',
					width: 22,
					handler: function() {
						ns.util.multiselect.selectAll(dataSetAvailable, dataSetSelected);
					}
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						ns.util.multiselect.select(this, dataSetSelected);
					}, this);
				}
			}
		});

		dataSetSelected = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-right',
			width: (ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			ddReorder: true,
			store: ns.store.dataSetSelected,
			tbar: [
				{
					xtype: 'button',
					icon: 'images/arrowleftdouble.png',
					width: 22,
					handler: function() {
						ns.util.multiselect.unselectAll(dataSetAvailable, dataSetSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowleft.png',
					width: 22,
					handler: function() {
						ns.util.multiselect.unselect(dataSetAvailable, dataSetSelected);
					}
				},
				'->',
				{
					xtype: 'label',
					text: NS.i18n.selected,
					cls: 'ns-toolbar-multiselect-right-label'
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						ns.util.multiselect.unselect(dataSetAvailable, this);
					}, this);
				}
			}
		});

		dataSet = {
			xtype: 'panel',
			title: '<div class="ns-panel-title-data">' + NS.i18n.reporting_rates + '</div>',
			hideCollapseTool: true,
			getDimension: function() {
				var config = {
					dimension: ns.conf.finals.dimension.dataSet.objectName,
					items: []
				};

				ns.store.dataSetSelected.each( function(r) {
					config.items.push({
						id: r.data.id,
						name: r.data.name
					});
				});

				return config.items.length ? config : null;
			},
			onExpand: function() {
				var h = ns.viewport.westRegion.hasScrollbar ?
					ns.conf.layout.west_scrollbarheight_accordion_dataset : ns.conf.layout.west_maxheight_accordion_dataset;
				ns.util.dimension.panel.setHeight(h);
				ns.util.multiselect.setHeight(
					[dataSetAvailable, dataSetSelected],
					this,
					ns.conf.layout.west_fill_accordion_dataset
				);

				if (!ns.store.dataSetAvailable.isLoaded) {
					ns.store.dataSetAvailable.load();
				}
			},
			items: [
				{
					xtype: 'panel',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						dataSetAvailable,
						dataSetSelected
					]
				}
			],
			listeners: {
				added: function() {
					ns.app.accordion.panels.push(this);
				},
				expand: function(p) {
					p.onExpand();
				}
			}
		};

		// period
		rewind = Ext.create('Ext.form.field.Checkbox', {
			relativePeriodId: 'rewind',
			boxLabel: 'Rewind one period',
			xable: function() {
				this.setDisabled(ns.util.checkbox.isAllFalse());
			}
		});

		relativePeriod = {
			xtype: 'panel',
			hideCollapseTool: true,
			autoScroll: true,
			bodyStyle: 'border:0 none',
			valueComponentMap: {},
			items: [
				{
					xtype: 'container',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						{
							xtype: 'panel',
							columnWidth: 0.34,
							bodyStyle: 'border-style:none; padding:0 0 0 8px',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											ns.cmp.dimension.relativePeriod.checkbox.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: NS.i18n.weeks,
									cls: 'ns-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_WEEK',
									boxLabel: NS.i18n.last_week
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_4_WEEKS',
									boxLabel: NS.i18n.last_4_weeks
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_12_WEEKS',
									boxLabel: NS.i18n.last_12_weeks
								}
							]
						},
						{
							xtype: 'panel',
							columnWidth: 0.33,
							bodyStyle: 'border-style:none',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											ns.cmp.dimension.relativePeriod.checkbox.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: NS.i18n.months,
									cls: 'ns-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_MONTH',
									boxLabel: NS.i18n.last_month
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_3_MONTHS',
									boxLabel: NS.i18n.last_3_months
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_12_MONTHS',
									boxLabel: NS.i18n.last_12_months,
									checked: true
								}
							]
						},
						{
							xtype: 'panel',
							columnWidth: 0.33,
							bodyStyle: 'border-style:none',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											ns.cmp.dimension.relativePeriod.checkbox.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: NS.i18n.bimonths,
									cls: 'ns-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_BIMONTH',
									boxLabel: NS.i18n.last_bimonth
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_6_BIMONTHS',
									boxLabel: NS.i18n.last_6_bimonths
								}
							]
						}
					]
				},
				{
					xtype: 'container',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						{
							xtype: 'panel',
							columnWidth: 0.34,
							bodyStyle: 'border-style:none; padding:5px 0 0 10px',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											ns.cmp.dimension.relativePeriod.checkbox.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: NS.i18n.quarters,
									cls: 'ns-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_QUARTER',
									boxLabel: NS.i18n.last_quarter
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_4_QUARTERS',
									boxLabel: NS.i18n.last_4_quarters
								}
							]
						},
						{
							xtype: 'panel',
							columnWidth: 0.33,
							bodyStyle: 'border-style:none; padding:5px 0 0',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											ns.cmp.dimension.relativePeriod.checkbox.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: NS.i18n.sixmonths,
									cls: 'ns-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_SIX_MONTH',
									boxLabel: NS.i18n.last_sixmonth
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_2_SIXMONTHS',
									boxLabel: NS.i18n.last_2_sixmonths
								}
							]
						},
						{
							xtype: 'panel',
							columnWidth: 0.33,
							bodyStyle: 'border-style:none; padding:5px 0 0',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											ns.cmp.dimension.relativePeriod.checkbox.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: NS.i18n.financial_years,
									cls: 'ns-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_FINANCIAL_YEAR',
									boxLabel: NS.i18n.last_financial_year
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_5_FINANCIAL_YEARS',
									boxLabel: NS.i18n.last_5_financial_years
								}
							]
						}

						//{
							//xtype: 'panel',
							//layout: 'anchor',
							//bodyStyle: 'border-style:none; padding:5px 0 0 46px',
							//defaults: {
								//labelSeparator: '',
								//style: 'margin-bottom:2px',
							//},
							//items: [
								//{
									//xtype: 'label',
									//text: 'Options',
									//cls: 'ns-label-period-heading-options'
								//},
								//rewind
							//]
						//}
					]
				},
				{
					xtype: 'container',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						{
							xtype: 'panel',
							columnWidth: 0.35,
							bodyStyle: 'border-style:none; padding:5px 0 0 10px',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											ns.cmp.dimension.relativePeriod.checkbox.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: NS.i18n.years,
									cls: 'ns-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'THIS_YEAR',
									boxLabel: NS.i18n.this_year
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_YEAR',
									boxLabel: NS.i18n.last_year
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_5_YEARS',
									boxLabel: NS.i18n.last_5_years
								}
							]
						}
					]
				}
			]
		};

		fixedPeriodAvailable = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-left',
			width: (ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding) / 2,
			height: 180,
			valueField: 'id',
			displayField: 'name',
			store: ns.store.fixedPeriodAvailable,
			tbar: [
				{
					xtype: 'label',
					text: NS.i18n.available,
					cls: 'ns-toolbar-multiselect-left-label'
				},
				'->',
				{
					xtype: 'button',
					icon: 'images/arrowright.png',
					width: 22,
					handler: function() {
						ns.util.multiselect.select(fixedPeriodAvailable, fixedPeriodSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowrightdouble.png',
					width: 22,
					handler: function() {
						ns.util.multiselect.selectAll(fixedPeriodAvailable, fixedPeriodSelected, true);
					}
				},
				' '
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						ns.util.multiselect.select(fixedPeriodAvailable, fixedPeriodSelected);
					}, this);
				}
			}
		});

		fixedPeriodSelected = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-right',
			width: (ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding) / 2,
			height: 180,
			valueField: 'id',
			displayField: 'name',
			ddReorder: false,
			store: ns.store.fixedPeriodSelected,
			tbar: [
				' ',
				{
					xtype: 'button',
					icon: 'images/arrowleftdouble.png',
					width: 22,
					handler: function() {
						ns.util.multiselect.unselectAll(fixedPeriodAvailable, fixedPeriodSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowleft.png',
					width: 22,
					handler: function() {
						ns.util.multiselect.unselect(fixedPeriodAvailable, fixedPeriodSelected);
					}
				},
				'->',
				{
					xtype: 'label',
					text: NS.i18n.selected,
					cls: 'ns-toolbar-multiselect-right-label'
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						ns.util.multiselect.unselect(fixedPeriodAvailable, fixedPeriodSelected);
					}, this);
				}
			}
		});

		period = {
			xtype: 'panel',
			title: '<div class="ns-panel-title-period">Periods</div>',
			hideCollapseTool: true,
			getDimension: function() {
				var config = {
						dimension: ns.conf.finals.dimension.period.objectName,
						items: []
					},
					chb = ns.cmp.dimension.relativePeriod.checkbox;

				ns.store.fixedPeriodSelected.each( function(r) {
					config.items.push({
						id: r.data.id,
						name: r.data.name
					});
				});

				for (var i = 0; i < chb.length; i++) {
					if (chb[i].getValue()) {
						config.items.push({
							id: chb[i].relativePeriodId,
							name: ''
						});
					}
				}

				return config.items.length ? config : null;
			},
			onExpand: function() {
				var h = ns.viewport.westRegion.hasScrollbar ?
					ns.conf.layout.west_scrollbarheight_accordion_period : ns.conf.layout.west_maxheight_accordion_period;
				ns.util.dimension.panel.setHeight(h);
				ns.util.multiselect.setHeight(
					[fixedPeriodAvailable, fixedPeriodSelected],
					this,
					ns.conf.layout.west_fill_accordion_period
				);
			},
			items: [
				{
					xtype: 'panel',
					layout: 'column',
					bodyStyle: 'border-style:none',
					style: 'margin-top:0px',
					items: [
						{
							xtype: 'combobox',
							cls: 'ns-combo',
							style: 'margin-bottom:2px',
							width: ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding - 62 - 62 - 4,
							valueField: 'id',
							displayField: 'name',
							emptyText: NS.i18n.select_period_type,
							editable: false,
							queryMode: 'remote',
							store: ns.store.periodType,
							periodOffset: 0,
							listeners: {
								select: function() {
									var nsype = new PeriodType(),
										periodType = this.getValue();

									var periods = nsype.get(periodType).generatePeriods({
										offset: this.periodOffset,
										filterFuturePeriods: true,
										reversePeriods: true
									});

									ns.store.fixedPeriodAvailable.setIndex(periods);
									ns.store.fixedPeriodAvailable.loadData(periods);
									ns.util.multiselect.filterAvailable(fixedPeriodAvailable, fixedPeriodSelected);
								}
							}
						},
						{
							xtype: 'button',
							text: NS.i18n.prev_year,
							style: 'margin-left:2px; border-radius:2px',
							height: 24,
							handler: function() {
								var cb = this.up('panel').down('combobox');
								if (cb.getValue()) {
									cb.periodOffset--;
									cb.fireEvent('select');
								}
							}
						},
						{
							xtype: 'button',
							text: NS.i18n.next_year,
							style: 'margin-left:2px; border-radius:2px',
							height: 24,
							handler: function() {
								var cb = this.up('panel').down('combobox');
								if (cb.getValue() && cb.periodOffset < 0) {
									cb.periodOffset++;
									cb.fireEvent('select');
								}
							}
						}
					]
				},
				{
					xtype: 'panel',
					layout: 'column',
					bodyStyle: 'border-style:none; padding-bottom:2px',
					items: [
						fixedPeriodAvailable,
						fixedPeriodSelected
					]
				},
				relativePeriod
			],
			listeners: {
				added: function() {
					ns.app.accordion.panels.push(this);
				},
				expand: function(p) {
					p.onExpand();
				}
			}
		};

		// organisation unit
		treePanel = Ext.create('Ext.tree.Panel', {
			cls: 'ns-tree',
			style: 'border-top: 1px solid #ddd; padding-top: 1px',
			width: ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding,
			rootVisible: false,
			autoScroll: true,
			multiSelect: true,
			rendered: false,
			reset: function() {
				var rootNode = this.getRootNode().findChild('id', ns.init.rootNodes[0].id);
				this.collapseAll();
				this.expandPath(rootNode.getPath());
				this.getSelectionModel().select(rootNode);
			},
			selectRootIf: function() {
				if (this.getSelectionModel().getSelection().length < 1) {
					var node = this.getRootNode().findChild('id', ns.init.rootNodes[0].id);
					if (this.rendered) {
						this.getSelectionModel().select(node);
					}
					return node;
				}
			},
			numberOfRecords: 0,
			recordsToSelect: [],
			multipleSelectIf: function(doUpdate) {
				if (this.recordsToSelect.length === this.numberOfRecords) {
					this.getSelectionModel().select(this.recordsToSelect);
					this.recordsToSelect = [];
					this.numberOfRecords = 0;

					if (doUpdate) {
						update();
					}
				}
			},
			multipleExpand: function(id, path, doUpdate) {
				var rootId = ns.conf.finals.root.id;

				if (path.substr(0, rootId.length + 1) !== ('/' + rootId)) {
					path = '/' + rootId + path;
				}

				this.expandPath('/' + path, 'id', '/', function() {
					var record = this.getRootNode().findChild('id', id, true);
					this.recordsToSelect.push(record);
					this.multipleSelectIf(doUpdate);
				}, this);
			},
            select: function(url, params) {
                if (!params) {
                    params = {};
                }
                Ext.Ajax.request({
                    url: url,
                    method: 'GET',
                    params: params,
                    scope: this,
                    success: function(r) {
                        var a = Ext.decode(r.responseText).organisationUnits;
                        this.numberOfRecords = a.length;
                        for (var i = 0; i < a.length; i++) {
                            this.multipleExpand(a[i].id, a[i].path);
                        }
                    }
                });
            },
			getParentGraphMap: function() {
				var selection = this.getSelectionModel().getSelection(),
					map = {};

				if (Ext.isArray(selection) && selection.length) {
					for (var i = 0, pathArray, key; i < selection.length; i++) {
						pathArray = selection[i].getPath().split('/');
						map[pathArray.pop()] = pathArray.join('/');
					}
				}

				return map;
			},
			selectGraphMap: function(map, doUpdate) {
				this.numberOfRecords = ns.support.prototype.object.getLength(map);

				for (var key in map) {
					if (map.hasOwnProperty(key)) {
						treePanel.multipleExpand(key, map[key], doUpdate);
					}
				}
			},
			store: Ext.create('Ext.data.TreeStore', {
				proxy: {
					type: 'ajax',
					url: ns.init.contextPath + ns.conf.finals.url.path_module + ns.conf.finals.url.organisationunitchildren_get
				},
				root: {
					id: ns.conf.finals.root.id,
					expanded: true,
					children: ns.init.rootNodes
				}
			}),
			xable: function(values) {
				for (var i = 0; i < values.length; i++) {
					if (!!values[i]) {
						this.disable();
						return;
					}
				}

				this.enable();
			},
			listeners: {
				load: function() {
					if (treePanel.tmpSelection) {
						treePanel.selectGraphMap(treePanel.tmpSelection);
					}
				},
				beforeitemexpand: function() {
					treePanel.tmpSelection = treePanel.getParentGraphMap();
				},
				added: function() {
					ns.cmp.dimension.organisationUnit.treepanel = this;
				},
				render: function() {
					this.rendered = true;
				},
				afterrender: function() {
					this.getSelectionModel().select(0);
				},
				itemcontextmenu: function(v, r, h, i, e) {
					v.getSelectionModel().select(r, false);

					if (v.menu) {
						v.menu.destroy();
					}
					v.menu = Ext.create('Ext.menu.Menu', {
						id: 'treepanel-contextmenu',
						showSeparator: false,
						shadow: false
					});
					if (!r.data.leaf) {
						v.menu.add({
							id: 'treepanel-contextmenu-item',
							text: NS.i18n.select_all_children,
							icon: 'images/node-select-child.png',
							handler: function() {
								r.expand(false, function() {
									v.getSelectionModel().select(r.childNodes, true);
									v.getSelectionModel().deselect(r);
								});
							}
						});
					}
					else {
						return;
					}

					v.menu.showAt(e.xy);
				}
			}
		});

		userOrganisationUnit = Ext.create('Ext.form.field.Checkbox', {
			columnWidth: 0.28,
			style: 'padding-top:2px; padding-left:3px; margin-bottom:0',
			boxLabel: NS.i18n.user_organisation_unit,
			labelWidth: ns.conf.layout.form_label_width,
			handler: function(chb, checked) {
				treePanel.xable([checked, userOrganisationUnitChildren.getValue(), userOrganisationUnitGrandChildren.getValue()]);
			}
		});

		userOrganisationUnitChildren = Ext.create('Ext.form.field.Checkbox', {
			columnWidth: 0.31,
			style: 'padding-top:2px; margin-bottom:0',
			boxLabel: NS.i18n.user_organisation_unit_children,
			labelWidth: ns.conf.layout.form_label_width,
			handler: function(chb, checked) {
				treePanel.xable([checked, userOrganisationUnit.getValue(), userOrganisationUnitGrandChildren.getValue()]);
			}
		});

		userOrganisationUnitGrandChildren = Ext.create('Ext.form.field.Checkbox', {
			columnWidth: 0.41,
			style: 'padding-top:2px; margin-bottom:0',
			boxLabel: NS.i18n.user_organisation_unit_grandchildren,
			labelWidth: ns.conf.layout.form_label_width,
			handler: function(chb, checked) {
				treePanel.xable([checked, userOrganisationUnit.getValue(), userOrganisationUnitChildren.getValue()]);
			}
		});

		organisationUnitLevel = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			multiSelect: true,
			style: 'margin-bottom:0',
			width: ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding - 38,
			valueField: 'level',
			displayField: 'name',
			emptyText: NS.i18n.select_organisation_unit_levels,
			editable: false,
			hidden: true,
			store: {
				fields: ['id', 'name', 'level'],
				data: ns.init.organisationUnitLevels
			}
		});

		organisationUnitGroup = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			multiSelect: true,
			style: 'margin-bottom:0',
			width: ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding - 38,
			valueField: 'id',
			displayField: 'name',
			emptyText: NS.i18n.select_organisation_unit_groups,
			editable: false,
			hidden: true,
			store: ns.store.organisationUnitGroup
		});

		toolMenu = Ext.create('Ext.menu.Menu', {
			shadow: false,
			showSeparator: false,
			menuValue: 'orgunit',
			clickHandler: function(param) {
				if (!param) {
					return;
				}

				var items = this.items.items;
				this.menuValue = param;

				// Menu item icon cls
				for (var i = 0; i < items.length; i++) {
					if (items[i].setIconCls) {
						if (items[i].param === param) {
							items[i].setIconCls('ns-menu-item-selected');
						}
						else {
							items[i].setIconCls('ns-menu-item-unselected');
						}
					}
				}

				// Gui
				if (param === 'orgunit') {
					userOrganisationUnit.show();
					userOrganisationUnitChildren.show();
					userOrganisationUnitGrandChildren.show();
					organisationUnitLevel.hide();
					organisationUnitGroup.hide();

					if (userOrganisationUnit.getValue() || userOrganisationUnitChildren.getValue()) {
						treePanel.disable();
					}
				}
				else if (param === 'level') {
					userOrganisationUnit.hide();
					userOrganisationUnitChildren.hide();
					userOrganisationUnitGrandChildren.hide();
					organisationUnitLevel.show();
					organisationUnitGroup.hide();
					treePanel.enable();
				}
				else if (param === 'group') {
					userOrganisationUnit.hide();
					userOrganisationUnitChildren.hide();
					userOrganisationUnitGrandChildren.hide();
					organisationUnitLevel.hide();
					organisationUnitGroup.show();
					treePanel.enable();
				}
			},
			items: [
				{
					xtype: 'label',
					text: 'Selection mode',
					style: 'padding:7px 5px 5px 7px; font-weight:bold; border:0 none'
				},
				{
					text: NS.i18n.select_organisation_units + '&nbsp;&nbsp;',
					param: 'orgunit',
					iconCls: 'ns-menu-item-selected'
				},
				{
					text: 'Select levels' + '&nbsp;&nbsp;',
					param: 'level',
					iconCls: 'ns-menu-item-unselected'
				},
				{
					text: 'Select groups' + '&nbsp;&nbsp;',
					param: 'group',
					iconCls: 'ns-menu-item-unselected'
				}
			],
			listeners: {
				afterrender: function() {
					this.getEl().addCls('ns-btn-menu');
				},
				click: function(menu, item) {
					this.clickHandler(item.param);
				}
			}
		});

		tool = Ext.create('Ext.button.Button', {
			cls: 'ns-button-organisationunitselection',
			iconCls: 'ns-button-icon-gear',
			width: 36,
			height: 24,
			menu: toolMenu
		});

		toolPanel = Ext.create('Ext.panel.Panel', {
			width: 36,
			bodyStyle: 'border:0 none; text-align:right',
			style: 'margin-right:2px',
			items: tool
		});

		organisationUnit = {
			xtype: 'panel',
			title: '<div class="ns-panel-title-organisationunit">' + NS.i18n.organisation_units + '</div>',
			bodyStyle: 'padding:2px',
			hideCollapseTool: true,
			collapsed: false,
			getDimension: function() {
				var r = treePanel.getSelectionModel().getSelection(),
					config = {
						dimension: ns.conf.finals.dimension.organisationUnit.objectName,
						items: []
					};

				if (toolMenu.menuValue === 'orgunit') {
					if (userOrganisationUnit.getValue() || userOrganisationUnitChildren.getValue() || userOrganisationUnitGrandChildren.getValue()) {
						if (userOrganisationUnit.getValue()) {
							config.items.push({
								id: 'USER_ORGUNIT',
								name: ''
							});
						}
						if (userOrganisationUnitChildren.getValue()) {
							config.items.push({
								id: 'USER_ORGUNIT_CHILDREN',
								name: ''
							});
						}
						if (userOrganisationUnitGrandChildren.getValue()) {
							config.items.push({
								id: 'USER_ORGUNIT_GRANDCHILDREN',
								name: ''
							});
						}
					}
					else {
						for (var i = 0; i < r.length; i++) {
							config.items.push({id: r[i].data.id});
						}
					}
				}
				else if (toolMenu.menuValue === 'level') {
					var levels = organisationUnitLevel.getValue();

					for (var i = 0; i < levels.length; i++) {
						config.items.push({
							id: 'LEVEL-' + levels[i],
							name: ''
						});
					}

					for (var i = 0; i < r.length; i++) {
						config.items.push({
							id: r[i].data.id,
							name: ''
						});
					}
				}
				else if (toolMenu.menuValue === 'group') {
					var groupIds = organisationUnitGroup.getValue();

					for (var i = 0; i < groupIds.length; i++) {
						config.items.push({
							id: 'OU_GROUP-' + groupIds[i],
							name: ''
						});
					}

					for (var i = 0; i < r.length; i++) {
						config.items.push({
							id: r[i].data.id,
							name: ''
						});
					}
				}

				return config.items.length ? config : null;
			},
            onExpand: function() {
                var h = ns.viewport.westRegion.hasScrollbar ?
                    ns.conf.layout.west_scrollbarheight_accordion_organisationunit : ns.conf.layout.west_maxheight_accordion_organisationunit;
                ns.util.dimension.panel.setHeight(h);
                treePanel.setHeight(this.getHeight() - ns.conf.layout.west_fill_accordion_organisationunit);
            },
            items: [
                {
                    layout: 'column',
                    bodyStyle: 'border:0 none',
                    style: 'padding-bottom:2px',
                    items: [
                        toolPanel,
                        {
                            width: ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding - 38,
                            layout: 'column',
                            bodyStyle: 'border:0 none',
                            items: [
                                userOrganisationUnit,
                                userOrganisationUnitChildren,
                                userOrganisationUnitGrandChildren,
                                organisationUnitLevel,
                                organisationUnitGroup
                            ]
                        }
                    ]
                },
                treePanel
            ],
            listeners: {
                added: function() {
                    ns.app.accordion.panels.push(this);
                },
                expand: function(p) {
                    p.onExpand();
                }
            }
        };

		// dimensions
		getDimensionPanels = function(dimensions, iconCls) {
			var	getAvailableStore,
				getSelectedStore,

				createPanel,
				getPanels;

			getAvailableStore = function(dimension) {
				return Ext.create('Ext.data.Store', {
					fields: ['id', 'name'],
					proxy: {
						type: 'ajax',
						url: ns.init.contextPath + '/api/dimensions/' + dimension.id + '.json',
						reader: {
							type: 'json',
							root: 'items'
						}
					},
					isLoaded: false,
					storage: {},
					sortStore: function() {
						this.sort('name', 'ASC');
					},
					reset: function() {
						if (this.isLoaded) {
							this.removeAll();
							ns.support.storage.load(this);
							this.sortStore();
						}
					},
					listeners: {
						load: function(s) {
							s.isLoaded = true;
							ns.support.storage.add(s);
						}
					}
				});
			};

			getSelectedStore = function() {
				return Ext.create('Ext.data.Store', {
					fields: ['id', 'name'],
					data: []
				});
			};

			createPanel = function(dimension) {
				var getAvailable,
					getSelected,

					availableStore,
					selectedStore,
					available,
					selected,

					panel;

				getAvailable = function(availableStore) {
					return Ext.create('Ext.ux.form.MultiSelect', {
						cls: 'ns-toolbar-multiselect-left',
						width: (ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding) / 2,
						valueField: 'id',
						displayField: 'name',
						store: availableStore,
						tbar: [
							{
								xtype: 'label',
								text: NS.i18n.available,
								cls: 'ns-toolbar-multiselect-left-label'
							},
							'->',
							{
								xtype: 'button',
								icon: 'images/arrowright.png',
								width: 22,
								handler: function() {
									ns.util.multiselect.select(available, selected);
								}
							},
							{
								xtype: 'button',
								icon: 'images/arrowrightdouble.png',
								width: 22,
								handler: function() {
									ns.util.multiselect.selectAll(available, selected);
								}
							}
						],
						listeners: {
							afterrender: function() {
								this.boundList.on('itemdblclick', function() {
									ns.util.multiselect.select(available, selected);
								}, this);
							}
						}
					});
				};

				getSelected = function(selectedStore) {
					return Ext.create('Ext.ux.form.MultiSelect', {
						cls: 'ns-toolbar-multiselect-right',
						width: (ns.conf.layout.west_fieldset_width - ns.conf.layout.west_width_padding) / 2,
						valueField: 'id',
						displayField: 'name',
						ddReorder: true,
						store: selectedStore,
						tbar: [
							{
								xtype: 'button',
								icon: 'images/arrowleftdouble.png',
								width: 22,
								handler: function() {
									ns.util.multiselect.unselectAll(available, selected);
								}
							},
							{
								xtype: 'button',
								icon: 'images/arrowleft.png',
								width: 22,
								handler: function() {
									ns.util.multiselect.unselect(available, selected);
								}
							},
							'->',
							{
								xtype: 'label',
								text: NS.i18n.selected,
								cls: 'ns-toolbar-multiselect-right-label'
							}
						],
						listeners: {
							afterrender: function() {
								this.boundList.on('itemdblclick', function() {
									ns.util.multiselect.unselect(available, selected);
								}, this);
							}
						}
					});
				};

				availableStore = getAvailableStore(dimension);
				selectedStore = getSelectedStore();

				dimensionIdAvailableStoreMap[dimension.id] = availableStore;
				dimensionIdSelectedStoreMap[dimension.id] = selectedStore;

				available = getAvailable(availableStore);
				selected = getSelected(selectedStore);

				availableStore.on('load', function() {
					ns.util.multiselect.filterAvailable(available, selected);
				});

				panel = {
					xtype: 'panel',
					title: '<div class="' + iconCls + '">' + dimension.name + '</div>',
					hideCollapseTool: true,
					availableStore: availableStore,
					selectedStore: selectedStore,
					getDimension: function() {
						var config = {
							dimension: dimension.id,
							items: []
						};

						selectedStore.each( function(r) {
							config.items.push({id: r.data.id});
						});

						return config.items.length ? config : null;
					},
					onExpand: function() {
						if (!availableStore.isLoaded) {
							availableStore.load();
						}

						var h = ns.viewport.westRegion.hasScrollbar ?
							ns.conf.layout.west_scrollbarheight_accordion_group : ns.conf.layout.west_maxheight_accordion_group;
						ns.util.dimension.panel.setHeight(h);

						ns.util.multiselect.setHeight(
							[available, selected],
							this,
							ns.conf.layout.west_fill_accordion_dataset
						);
					},
					items: [
						{
							xtype: 'panel',
							layout: 'column',
							bodyStyle: 'border-style:none',
							items: [
								available,
								selected
							]
						}
					],
					listeners: {
						added: function() {
							ns.app.accordion.panels.push(this);
						},
						expand: function(p) {
							p.onExpand();
						}
					}
				};

				return panel;
			};

			getPanels = function() {
				var panels = [];

				for (var i = 0, panel; i < dimensions.length; i++) {
					panel = createPanel(dimensions[i]);

					panels.push(panel);
				}

				return panels;
			};

			return getPanels();
		};

		// viewport
		update = function() {
			var config = ns.engine.getLayoutConfig(),
				layout = ns.api.layout.Layout(config);

			if (!layout) {
				return;
			}

			ns.engine.createTable(layout, ns);
		};

		accordionBody = Ext.create('Ext.panel.Panel', {
			layout: 'accordion',
			activeOnTop: true,
			cls: 'ns-accordion',
			bodyStyle: 'border:0 none; margin-bottom:2px',
			height: 700,
			items: function() {
				var panels = [
					indicator,
					dataElement,
					dataSet,
					period,
					organisationUnit
				],
				dims = Ext.clone(ns.init.dimensions);

				ns.support.prototype.array.sortObjectsByObjectKey(dims);

				panels = panels.concat(getDimensionPanels(dims, 'ns-panel-title-dimension'));

				last = panels[panels.length - 1];
				last.cls = 'ns-accordion-last';

				return panels;
			}()
		});

		accordion = Ext.create('Ext.panel.Panel', {
			bodyStyle: 'border-style:none; padding:2px; padding-bottom:0; overflow-y:scroll;',
			items: accordionBody,
			panels: [],
			setThisHeight: function(mx) {
				var panelHeight = this.panels.length * 28,
					height;

				if (westRegion.hasScrollbar) {
					height = panelHeight + mx;
					this.setHeight(viewport.getHeight() - 2);
					accordionBody.setHeight(height - 2);
				}
				else {
					height = westRegion.getHeight() - conf.layout.west_fill;
					mx += panelHeight;
					accordion.setHeight((height > mx ? mx : height) - 2);
					accordionBody.setHeight((height > mx ? mx : height) - 2);
				}
			},
			getExpandedPanel: function() {
				for (var i = 0, panel; i < this.panels.length; i++) {
					if (!this.panels[i].collapsed) {
						return this.panels[i];
					}
				}

				return null;
			},
			getFirstPanel: function() {
				return this.panels[0];
			},
			listeners: {
				added: function() {
					ns.app.accordion = this;
				}
			}
		});

		westRegion = Ext.create('Ext.panel.Panel', {
			region: 'west',
			preventHeader: true,
			collapsible: true,
			collapseMode: 'mini',
			width: function() {
				if (Ext.isWebKit) {
					return ns.conf.layout.west_width + 8;
				}
				else {
					if (Ext.isLinux && Ext.isGecko) {
						return ns.conf.layout.west_width + 13;
					}
					return ns.conf.layout.west_width + 17;
				}
			}(),
			items: accordion,
			listeners: {
				added: function() {
					ns.app.westRegion = this;
				}
			}
		});

		layoutButton = Ext.create('Ext.button.Button', {
			text: 'Layout',
			menu: {},
			handler: function() {
				if (!ns.app.layoutWindow) {
					ns.app.layoutWindow = LayoutWindow();
				}

				ns.app.layoutWindow.show();
			}
		});

		optionsButton = Ext.create('Ext.button.Button', {
			text: 'Options',
			menu: {},
			handler: function() {
				if (!ns.app.optionsWindow) {
					ns.app.optionsWindow = OptionsWindow();
				}

				ns.app.optionsWindow.show();
			}
		});

		favoriteButton = Ext.create('Ext.button.Button', {
			text: 'Favorites',
			menu: {},
			handler: function() {
				if (ns.app.favoriteWindow) {
					ns.app.favoriteWindow.destroy();
				}

				ns.app.favoriteWindow = FavoriteWindow();
				ns.app.favoriteWindow.show();
			}
		});

		openTableLayoutTab = function(type, isNewTab) {
			if (ns.init.contextPath && ns.paramString) {
				var url = ns.init.contextPath + '/api/analytics.' + type + ns.engine.getParamString(ns.xLayout);
				url += '&tableLayout=true&columns=' + ns.xLayout.columnDimensionNames.join(';') + '&rows=' + ns.xLayout.rowDimensionNames.join(';');

				window.open(url, isNewTab ? '_blank' : '_top');
			}
		};

		downloadButton = Ext.create('Ext.button.Button', {
			text: 'Download',
			disabled: true,
			menu: {
				cls: 'ns-menu',
				shadow: false,
				showSeparator: false,
				items: [
					{
						xtype: 'label',
						text: NS.i18n.table_layout,
						style: 'padding:7px 5px 5px 7px; font-weight:bold; border:0 none'
					},
					{
						text: 'Microsoft Excel (.xls)',
						iconCls: 'ns-menu-item-tablelayout',
						handler: function() {
							openTableLayoutTab('xls');
						}
					},
					{
						text: 'CSV (.csv)',
						iconCls: 'ns-menu-item-tablelayout',
						handler: function() {
							openTableLayoutTab('csv');
						}
					},
					{
						text: 'HTML (.html)',
						iconCls: 'ns-menu-item-tablelayout',
						handler: function() {
							openTableLayoutTab('html', true);
						}
					},
					{
						xtype: 'label',
						text: NS.i18n.plain_data_sources,
						style: 'padding:7px 5px 5px 7px; font-weight:bold'
					},
					{
						text: 'JSON',
						iconCls: 'ns-menu-item-datasource',
						handler: function() {
							if (ns.init.contextPath && ns.paramString) {
								window.open(ns.init.contextPath + '/api/analytics.json' + ns.paramString, '_blank');
							}
						}
					},
					{
						text: 'XML',
						iconCls: 'ns-menu-item-datasource',
						handler: function() {
							if (ns.init.contextPath && ns.paramString) {
								window.open(ns.init.contextPath + '/api/analytics.xml' + ns.paramString, '_blank');
							}
						}
					},
					{
						text: 'Microsoft Excel',
						iconCls: 'ns-menu-item-datasource',
						handler: function() {
							if (ns.init.contextPath && ns.paramString) {
								window.location.href = ns.init.contextPath + '/api/analytics.xls' + ns.paramString;
							}
						}
					},
					{
						text: 'CSV',
						iconCls: 'ns-menu-item-datasource',
						handler: function() {
							if (ns.init.contextPath && ns.paramString) {
								window.location.href = ns.init.contextPath + '/api/analytics.csv' + ns.paramString;
							}
						}
					},
					{
						text: 'JRXML',
						iconCls: 'ns-menu-item-datasource',
						handler: function() {
							if (ns.init.contextPath && ns.paramString) {
								window.open(ns.init.contextPath + '/api/analytics.jrxml' + ns.paramString, '_blank');
							}
						}
					}
				],
				listeners: {
					afterrender: function() {
						this.getEl().addCls('ns-toolbar-btn-menu');
					}
				}
			}
		});

		interpretationButton = Ext.create('Ext.button.Button', {
			text: NS.i18n.share,
			menu: {},
			disabled: true,
			xable: function() {
				if (ns.favorite) {
					this.enable();
					this.disabledTooltip.destroy();
				}
				else {
					if (ns.xLayout) {
						this.disable();
						this.createTooltip();
					}
				}
			},
			disabledTooltip: null,
			createTooltip: function() {
				this.disabledTooltip = Ext.create('Ext.tip.ToolTip', {
					target: this.getEl(),
					html: NS.i18n.save_load_favorite_before_sharing,
					'anchor': 'bottom'
				});
			},
			handler: function() {
				if (ns.viewport.interpretationWindow) {
					ns.viewport.interpretationWindow.destroy();
				}

				ns.viewport.interpretationWindow = NS.app.InterpretationWindow();

				if (ns.viewport.interpretationWindow) {
					ns.viewport.interpretationWindow.show();
				}
			}
		});

		defaultButton = Ext.create('Ext.button.Button', {
			text: NS.i18n.table,
			iconCls: 'ns-button-icon-table',
			toggleGroup: 'module',
			pressed: true,
			handler: function() {
				if (!this.pressed) {
					this.toggle();
				}
			}
		});

		centerRegion = Ext.create('Ext.panel.Panel', {
			region: 'center',
			bodyStyle: 'padding:1px',
			autoScroll: true,
			tbar: {
				defaults: {
					height: 26
				},
				items: [
					{
						text: '<<<',
						handler: function(b) {
							var text = b.getText();
							text = text === '<<<' ? '>>>' : '<<<';
							b.setText(text);

							westRegion.toggleCollapse();
						}
					},
					{
						text: '<b>' + NS.i18n.update + '</b>',
						handler: function() {
							update();
						}
					},
					layoutButton,
					optionsButton,
					{
						xtype: 'tbseparator',
						height: 18,
						style: 'border-color:transparent; border-right-color:#d1d1d1; margin-right:4px',
					},
					favoriteButton,
					downloadButton,
					interpretationButton,
					'->',
					defaultButton,
					{
						text: NS.i18n.chart,
						iconCls: 'ns-button-icon-chart',
						toggleGroup: 'module',
						menu: {},
						handler: function(b) {
							b.menu = Ext.create('Ext.menu.Menu', {
								closeAction: 'destroy',
								shadow: false,
								showSeparator: false,
								items: [
									{
										text: 'Go to charts' + '&nbsp;&nbsp;', //i18n
										cls: 'ns-menu-item-noicon',
										handler: function() {
											window.location.href = ns.init.contextPath + '/dhis-web-visualizer/app/index.html';
										}
									},
									'-',
									{
										text: 'Open this table as chart' + '&nbsp;&nbsp;', //i18n
										cls: 'ns-menu-item-noicon',
										disabled: !(NS.isSessionStorage && ns.layout),
										handler: function() {
											if (NS.isSessionStorage) {
												ns.layout.parentGraphMap = treePanel.getParentGraphMap();
												ns.engine.setSessionStorage('analytical', ns.layout, ns.init.contextPath + '/dhis-web-visualizer/app/index.html?s=analytical');
											}
										}
									},
									{
										text: 'Open last chart' + '&nbsp;&nbsp;', //i18n
										cls: 'ns-menu-item-noicon',
										disabled: !(NS.isSessionStorage && JSON.parse(sessionStorage.getItem('dhis2')) && JSON.parse(sessionStorage.getItem('dhis2'))['chart']),
										handler: function() {
											window.location.href = ns.init.contextPath + '/dhis-web-visualizer/app/index.html?s=chart';
										}
									}
								],
								listeners: {
									show: function() {
										ns.util.window.setAnchorPosition(b.menu, b);
									},
									hide: function() {
										b.menu.destroy();
										defaultButton.toggle();
									},
									destroy: function(m) {
										b.menu = null;
									}
								}
							});

							b.menu.show();
						}
					},
					{
						text: NS.i18n.map,
						iconCls: 'ns-button-icon-map',
						toggleGroup: 'module',
						menu: {},
						handler: function(b) {
							b.menu = Ext.create('Ext.menu.Menu', {
								closeAction: 'destroy',
								shadow: false,
								showSeparator: false,
								items: [
									{
										text: 'Go to maps' + '&nbsp;&nbsp;', //i18n
										cls: 'ns-menu-item-noicon',
										handler: function() {
											window.location.href = ns.init.contextPath + '/dhis-web-mapping/app/index.html';
										}
									},
									'-',
									{
										text: 'Open this table as map' + '&nbsp;&nbsp;', //i18n
										cls: 'ns-menu-item-noicon',
										disabled: !(NS.isSessionStorage && ns.layout),
										handler: function() {
											if (NS.isSessionStorage) {
												ns.layout.parentGraphMap = treePanel.getParentGraphMap();
												ns.engine.setSessionStorage('analytical', ns.layout, ns.init.contextPath + '/dhis-web-mapping/app/index.html?s=analytical');
											}
										}
									},
									{
										text: 'Open last map' + '&nbsp;&nbsp;', //i18n
										cls: 'ns-menu-item-noicon',
										disabled: !(NS.isSessionStorage && JSON.parse(sessionStorage.getItem('dhis2')) && JSON.parse(sessionStorage.getItem('dhis2'))['map']),
										handler: function() {
											window.location.href = ns.init.contextPath + '/dhis-web-mapping/app/index.html?s=chart';
										}
									}
								],
								listeners: {
									show: function() {
										ns.util.window.setAnchorPosition(b.menu, b);
									},
									hide: function() {
										b.menu.destroy();
										defaultButton.toggle();
									},
									destroy: function(m) {
										b.menu = null;
									}
								}
							});

							b.menu.show();
						}
					},
					{
						xtype: 'tbseparator',
						height: 18,
						style: 'border-color:transparent; border-right-color:#d1d1d1; margin-right:4px',
					},
					{
						xtype: 'button',
						text: NS.i18n.home,
						handler: function() {
							window.location.href = ns.init.contextPath + '/dhis-web-commons-about/redirect.action';
						}
					}
				]
			},
			listeners: {
				added: function() {
					ns.app.centerRegion = this;
				},
				afterrender: function(p) {
					var liStyle = 'padding:3px 10px; color:#333',
						html = '';

					html += '<div style="padding:20px">';
					html += '<div style="font-size:14px; padding-bottom:8px">' + NS.i18n.example1 + '</div>';
					html += '<div style="' + liStyle + '">- ' + NS.i18n.example2 + '</div>';
					html += '<div style="' + liStyle + '">- ' + NS.i18n.example3 + '</div>';
					html += '<div style="' + liStyle + '">- ' + NS.i18n.example4 + '</div>';
					html += '<div style="font-size:14px; padding-top:20px; padding-bottom:8px">' + NS.i18n.example5 + '</div>';
					html += '<div style="' + liStyle + '">- ' + NS.i18n.example6 + '</div>';
					html += '<div style="' + liStyle + '">- ' + NS.i18n.example7 + '</div>';
					html += '<div style="' + liStyle + '">- ' + NS.i18n.example8 + '</div>';
					html += '</div>';

					p.update(html);
				}
			}
		});

		setGui = function(layout, xLayout, updateGui, isFavorite) {
			var dimensions = [].concat(layout.columns || [], layout.rows || [], layout.filters || []),
				dimMap = ns.service.layout.getObjectNameDimensionMapFromDimensionArray(dimensions),
				recMap = ns.service.layout.getObjectNameDimensionItemsMapFromDimensionArray(dimensions),
				graphMap = layout.parentGraphMap,
				objectName,
				periodRecords,
				fixedPeriodRecords = [],
				dimNames = [],
				isOu = false,
				isOuc = false,
				isOugc = false,
				levels = [],
				groups = [],
				orgunits = [];

			// State
			downloadButton.enable();

			if (isFavorite) {
				interpretationButton.enable();
			}

			// Set gui
			if (!updateGui) {
				return;
			}

			// Indicators
			ns.store.indicatorSelected.removeAll();
			objectName = dimConf.indicator.objectName;
			if (dimMap[objectName]) {
				ns.store.indicatorSelected.add(Ext.clone(recMap[objectName]));
				ns.util.multiselect.filterAvailable({store: ns.store.indicatorAvailable}, {store: ns.store.indicatorSelected});
			}

			// Data elements
			ns.store.dataElementSelected.removeAll();
			objectName = dimConf.dataElement.objectName;
			if (dimMap[objectName]) {
				ns.store.dataElementSelected.add(Ext.clone(recMap[objectName]));
				ns.util.multiselect.filterAvailable({store: ns.store.dataElementAvailable}, {store: ns.store.dataElementSelected});
				dataElementDetailLevel.setValue(objectName);
			}

			// Operands
			objectName = dimConf.operand.objectName;
			if (dimMap[objectName]) {
				ns.store.dataElementSelected.add(Ext.clone(recMap[objectName]));
				ns.util.multiselect.filterAvailable({store: ns.store.dataElementAvailable}, {store: ns.store.dataElementSelected});
				dataElementDetailLevel.setValue(objectName);
			}

			// Data sets
			ns.store.dataSetSelected.removeAll();
			objectName = dimConf.dataSet.objectName;
			if (dimMap[objectName]) {
				ns.store.dataSetSelected.add(Ext.clone(recMap[objectName]));
				ns.util.multiselect.filterAvailable({store: ns.store.dataSetAvailable}, {store: ns.store.dataSetSelected});
			}

			// Periods
			ns.store.fixedPeriodSelected.removeAll();
			ns.util.checkbox.setAllFalse();
			periodRecords = recMap[dimConf.period.objectName] || [];
			for (var i = 0, peroid, checkbox; i < periodRecords.length; i++) {
				period = periodRecords[i];
				checkbox = relativePeriod.valueComponentMap[period.id];
				if (checkbox) {
					checkbox.setValue(true);
				}
				else {
					fixedPeriodRecords.push(period);
				}
			}
			ns.store.fixedPeriodSelected.add(fixedPeriodRecords);
			ns.util.multiselect.filterAvailable({store: ns.store.fixedPeriodAvailable}, {store: ns.store.fixedPeriodSelected});

			// Group sets
			for (var key in dimensionIdSelectedStoreMap) {
				if (dimensionIdSelectedStoreMap.hasOwnProperty(key)) {
					var a = dimensionIdAvailableStoreMap[key],
						s = dimensionIdSelectedStoreMap[key];

					if (s.getCount() > 0) {
						a.reset();
						s.removeAll();
					}

					if (recMap[key]) {
						s.add(recMap[key]);
						ns.util.multiselect.filterAvailable({store: a}, {store: s});
					}
				}
			}

			// Layout
			ns.viewport.dimensionStore.reset(true);
			ns.viewport.colStore.removeAll();
			ns.viewport.rowStore.removeAll();
			ns.viewport.filterStore.removeAll();

			if (layout.columns) {
				dimNames = [];

				for (var i = 0, dim; i < layout.columns.length; i++) {
					dim = dimConf.objectNameMap[layout.columns[i].dimension];

					if (!Ext.Array.contains(dimNames, dim.dimensionName)) {
						ns.viewport.colStore.add({
							id: dim.dimensionName,
							name: dimConf.objectNameMap[dim.dimensionName].name
						});

						dimNames.push(dim.dimensionName);
					}

					ns.viewport.dimensionStore.remove(ns.viewport.dimensionStore.getById(dim.dimensionName));
				}
			}

			if (layout.rows) {
				dimNames = [];

				for (var i = 0, dim; i < layout.rows.length; i++) {
					dim = dimConf.objectNameMap[layout.rows[i].dimension];

					if (!Ext.Array.contains(dimNames, dim.dimensionName)) {
						ns.viewport.rowStore.add({
							id: dim.dimensionName,
							name: dimConf.objectNameMap[dim.dimensionName].name
						});

						dimNames.push(dim.dimensionName);
					}

					ns.viewport.dimensionStore.remove(ns.viewport.dimensionStore.getById(dim.dimensionName));
				}
			}

			if (layout.filters) {
				dimNames = [];

				for (var i = 0, dim; i < layout.filters.length; i++) {
					dim = dimConf.objectNameMap[layout.filters[i].dimension];

					if (!Ext.Array.contains(dimNames, dim.dimensionName)) {
						ns.viewport.filterStore.add({
							id: dim.dimensionName,
							name: dimConf.objectNameMap[dim.dimensionName].name
						});

						dimNames.push(dim.dimensionName);
					}

					ns.viewport.dimensionStore.remove(ns.viewport.dimensionStore.getById(dim.dimensionName));
				}
			}

			// Options
			if (ns.viewport.optionsWindow) {
				ns.viewport.optionsWindow.setOptions(layout);
			}

			// Organisation units
			if (recMap[dimConf.organisationUnit.objectName]) {
				for (var i = 0, ouRecords = recMap[dimConf.organisationUnit.objectName]; i < ouRecords.length; i++) {
					if (ouRecords[i].id === 'USER_ORGUNIT') {
						isOu = true;
					}
					else if (ouRecords[i].id === 'USER_ORGUNIT_CHILDREN') {
						isOuc = true;
					}
					else if (ouRecords[i].id === 'USER_ORGUNIT_GRANDCHILDREN') {
						isOugc = true;
					}
					else if (ouRecords[i].id.substr(0,5) === 'LEVEL') {
						levels.push(parseInt(ouRecords[i].id.split('-')[1]));
					}
					else if (ouRecords[i].id.substr(0,8) === 'OU_GROUP') {
						groups.push(parseInt(ouRecords[i].id.split('-')[1]));
					}
					else {
						orgunits.push(ouRecords[i].id);
					}
				}
			}

			if (levels.length) {
				toolMenu.clickHandler('level');
				organisationUnitLevel.setValue(levels);
			}
			else if (groups.length) {
				toolMenu.clickHandler('group');
				organisationUnitGroup.setValue(groups);
			}
			else {
				toolMenu.clickHandler('orgunit');
				userOrganisationUnit.setValue(isOu);
				userOrganisationUnitChildren.setValue(isOuc);
				userOrganisationUnitGrandChildren.setValue(isOugc);
			}

			if (!(isOu || isOuc || isOugc)) {
				if (Ext.isObject(graphMap)) {
					treePanel.selectGraphMap(graphMap);
				}
			}
			else {
				treePanel.reset();
			}
		};

		viewport = Ext.create('Ext.container.Viewport', {
			layout: 'border',
			accordion: accordion,
			accordionBody: accordionBody,
			westRegion: westRegion,
			centerRegion: centerRegion,
			updateViewport: update,
			layoutButton: layoutButton,
			optionsButton: optionsButton,
			favoriteButton: favoriteButton,
			downloadButton: downloadButton,
			interpretationButton: interpretationButton,
			userOrganisationUnit: userOrganisationUnit,
			userOrganisationUnitChildren: userOrganisationUnitChildren,
			dataElementDetailLevel: dataElementDetailLevel,
			treePanel: treePanel,
			setGui: setGui,
			items: [
				westRegion,
				centerRegion
			],
			listeners: {
				render: function(vp) {
					ns.app.viewport = vp;

					ns.app.layoutWindow = LayoutWindow();
					ns.app.layoutWindow.hide();
					ns.app.optionsWindow = OptionsWindow();
					ns.app.optionsWindow.hide();
				},
				afterrender: function() {
					ns.init.afterRender();
				}
			}
		});

		// add listeners
		(function() {
			ns.store.indicatorAvailable.on('load', function() {
				ns.util.multiselect.filterAvailable(indicatorAvailable, indicatorSelected);
			});

			ns.store.dataElementAvailable.on('load', function() {
				ns.util.multiselect.filterAvailable(dataElementAvailable, dataElementSelected);
			});

			ns.store.dataSetAvailable.on('load', function(s) {
				ns.util.multiselect.filterAvailable(dataSetAvailable, dataSetSelected);
				s.sort('name', 'ASC');
			});
		}());

		return viewport;
	};

	update = function(layout) {
		var xLayout,
			paramString,
			tableUuid;

		if (!layout) {
			return;
		}

		xLayout = ns.core.service.layout.getExtendedLayout(layout);
		paramString = web.analytics.getParamString(xLayout, true);
		tableUuid = ns.init.el + '_' + Ext.data.IdGenerator.get('uuid').generate(),

		if (!web.analytics.validateUrl(init.contextPath + '/api/analytics.json' + paramString)) {
			return;
		}

		ns.core.web.mask.show(ns.app.centerRegion);

		Ext.Ajax.request({
			url: init.contextPath + '/api/analytics.json' + paramString,
			timeout: 60000,
			headers: {
				'Content-Type': 'application/json',
				'Accens': 'application/json'
			},
			disableCaching: false,
			failure: function(r) {
				web.mask.hide(ns.app.centerRegion);
				alert(r.responseText);
			},
			success: function(r) {
				var response = ns.core.api.response.Response(Ext.decode(r.responseText)),
					xResponse,
					xColAxis,
					xRowAxis,
					config;

				if (!response) {
					ns.core.web.mask.hide(ns.app.centerRegion);
					return;
				}

				// sync xLayout with response
				xLayout = ns.core.service.layout.getSyncronizedXLayout(xLayout, response);

				if (!xLayout) {
					ns.core.web.mask.hide(ns.app.centerRegion);
					return;
				}

				// extend response
				xResponse = ns.core.service.response.getExtendedResponse(response, xLayout);

				// extended axes
				xColAxis = ns.core.service.layout.getExtendedAxis('col', xLayout.columnDimensionNames, xResponse);
				xRowAxis = ns.core.service.layout.getExtendedAxis('row', xLayout.rowDimensionNames, xResponse);

				// update viewport
				config = web.pivot.getHtml(layout, xResponse, xColAxis, xRowAxis);
				ns.app.centerRegion.removeAll(true);
				ns.app.centerRegion.update(config.html);

				// after render
				ns.app.layout = layout;
				ns.app.xLayout = xLayout;
				ns.app.response = response;
				ns.app.xResponse = xResponse;
				ns.app.uuidObjectMap = Ext.applyIf((xColAxis ? xColAxis.uuidObjectMap : {}), (xRowAxis ? xRowAxis.uuidObjectMap : {}));
				ns.app.uuidDimUuidsMap = config.uuidDimUuidsMap;

				if (NS.isSessionStorage) {
					setMouseHandlers(layout, response, ns.app.uuidDimUuidsMap, ns.app.uuidObjectMap);
					ns.core.web.storage.session.set(layout, 'table');
				}

				ns.app.viewport.setGui(layout, xLayout, isUpdateGui);

				ns.core.web.mask.hide(ns.app.centerRegion);

				if (NS.isDebug) {
					console.log("app", ns.app);
				}
			}
		});
	};

	// initialize
	(function() {
		var requests = [],
			callbacks = 0,
			init = {},
			fn;

		init.user = {};

		fn = function() {
			if (++callbacks === requests.length) {

				ns.core = extendCore(NS.getCore(init));

				ns.app.viewport = createViewport();
			}
		};

		// requests
		Ext.Ajax.request({
			url: '../manifest.webapp',
			success: function(r) {
				init.contextPath = '../..'; //init.contextPath = Ext.decode(r.responseText).activities.dhis.href;

				// requests
				requests.push({
					url: init.contextPath + '/api/organisationUnits.json?userOnly=true&viewClass=detailed&links=false',
					success: function(r) {
						var ou = Ext.decode(r.responseText).organisationUnits[0];
						init.user.ou = ou.id;
						init.user.ouc = Ext.Array.pluck(ou.children, 'id');
						fn();
					}
				});

				requests.push({
					url: init.contextPath + '/api/mapLegendSets.json?viewClass=detailed&links=false&paging=false',
					success: function(r) {
						init.legendSets = Ext.decode(r.responseText).mapLegendSets;
						fn();
					}
				});

				requests.push({
					url: init.contextPath + '/api/dimensions.json?links=false&paging=false',
					success: function(r) {
						init.dimensions = Ext.decode(r.responseText).dimensions;
						fn();
					}
				});

				requests.push({
					url: init.contextPath + '/dhis-web-pivot/i18n.action',
					success: function(r) {
						NS.i18n = Ext.decode(r.responseText);
						fn();
					}
				});

				for (var i = 0; i < requests.length; i++) {
					Ext.Ajax.request(requests[i]);
				}
			}
		});
	}());
});
