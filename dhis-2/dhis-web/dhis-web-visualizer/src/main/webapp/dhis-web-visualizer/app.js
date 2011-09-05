Ext.onReady( function() {
    
    DV = {
        
        util: {
            getCmp: function(q) {
                return DV.viewport.query(q)[0];
            },
            getViewportSize: function() {
                var c = DV.getCmp('panel[region="center"]');
                return { x: c.getWidth(), y: c.getHeight() };
            }
        },
        
        chart: {
            chart: null,
            
            data: null,
            
            getData: function() {
                this.data = [
                    { x: 'August 2010', 'anc 1': 12, anc2: 12, anc3: 5, anc4: 3 },
                    { x: 'September 2010', 'anc 2': 5, anc2: 23, anc3: 16, anc4: 5 },
                    { x: 'October 2010', 'anc 3': 21, anc2: 6, anc3: 11, anc4: 16 },
                    { x: 'November 2010', 'anc 4': 15, anc2: 22, anc3: 16, anc4: 5 }
                ];
                
                return this.data;
            },
            
            store: null,
            
            getStore: function() {
                var properties = [];
                for (var p in this.data[0]) {
                    properties.push(p);
                }
                                
                Ext.define('model1', {
                    extend: 'Ext.data.Model',
                    fields: properties
                });

                this.store = Ext.create('Ext.data.Store', {
                    model: 'model1',
                    data: this.data
                });
                
                this.store.bottom = properties.slice(0, 1);
                this.store.left = properties.slice(1, properties.length);
                
                return this.store;
            },
            
            chart: null,       
        
            getChart: function() {
                this.chart = Ext.create('Ext.chart.Chart', {
                    width: DV.util.getViewportSize.x,
                    height: DV.util.getViewportSize.y,
                    animate: true,
                    store: this.store,
                    legend: {
                        position: 'bottom'
                    },
                    axes: [
                        {
                            title: 'Value',
                            type: 'Numeric',
                            position: 'left',
                            minimum: 0,
                            grid: true,
                            fields: this.store.left,
                            label: {
                                renderer: Ext.util.Format.numberRenderer('0,0')
                            }
                        },
                        {
                            title: 'Indicator',
                            type: 'Category',
                            position: 'bottom',
                            fields: this.store.bottom
                        }
                    ],
                    series: [
                        {
                            type: 'column',
                            axis: 'left',
                            xField: this.store.bottom,
                            yField: this.store.left
                        }
                    ]
                });
                
                return this.chart;
            }
        },
        
        viewport: Ext.create('Ext.container.Viewport', {
            layout: 'border',
            renderTo: Ext.getBody(),
            items: [
                {
                    region: 'west',
                    width: 300,
                    preventHeader: true,
                    collapsible: true,
                    collapseMode: 'mini',
                    tbar: [
                        {
                            xtype: 'label',
                            text: 'Chart settings',
                            style: 'font-weight:bold; padding:0 5px'
                        },
                        {
                            xtype: 'button',
                            text: 'Column',
                            toggleGroup: 'settings',
                            pressed: true,
                            handler: function(b) {
                                if (!b.pressed) {
                                    b.toggle();
                                }
                            }
                        },
                        {
                            xtype: 'button',
                            text: 'Pie',
                            toggleGroup: 'settings',
                            handler: function(b) {
                                if (!b.pressed) {
                                    b.toggle();
                                }
                            }
                        },
                        {
                            xtype: 'button',
                            text: 'Line',
                            toggleGroup: 'settings',
                            handler: function(b) {
                                if (!b.pressed) {
                                    b.toggle();
                                }
                            }
                        }
                    ],
                    listeners: {
                        collapse: function(p) {                    
                            p.collapsed = true;
                            DV.util.getCmp('button[name="resize"]').setText('>>');
                        },
                        expand: function(p) {
                            p.collapsed = false;
                            DV.util.getCmp('button[name="resize"]').setText('<<');
                        }
                    }
                },
                {
                    region: 'center',
                    layout: 'fit',
                    bodyStyle: 'padding:10px',
                    tbar: [
                        {
                            xtype: 'button',
                            name: 'resize',
                            text: '<<',
                            toolTip: 'Collapse',
                            handler: function() {
                                var p = DV.util.getCmp('panel[region="west"]');
                                if (p.collapsed) {
                                    p.expand();
                                }
                                else {
                                    p.collapse();
                                }
                            }
                        },
                        {
                            xtype: 'label',
                            text: 'DHIS 2 Data Visualizer',
                            style: 'font-weight:bold; padding:0 2px'
                        },
                        {
                            xtype: 'button',
                            text: 'Load..',
                            handler: function() {
                                var c = DV.util.getCmp('panel[region="center"]');
                                
                                DV.chart.getData();
                                
                                DV.chart.getStore();
                                
                                var chart = DV.chart.getChart();
                                
                                c.add(chart);
                            }
                        },
                        {
                            xtype: 'button',
                            text: 'Reload..',
                            handler: function() {
                                var c = DV.util.getCmp('panel[region="center"]');
                                var sd = [
                                    { anc1: 16, anc2: 14, anc3: 7, anc4: 4, anc5: 11, p: 'August 2010'},
                                    { anc1: 7, anc2: 17, anc3: 4, anc4: 5, anc5: 17, p: 'September 2010'},
                                    { anc1: 15, anc2: 7, anc3: 13, anc4: 14, anc5: 7, p: 'October 2010'}
                                ];
                                
                                var sf = [];
                                for (var r in sd[0]) {
                                    sf.push(r);
                                }
                                
                                Ext.define('model1', {
                                    extend: 'Ext.data.Model',
                                    fields: sf
                                });
                                
                                var lf = sf.slice(0,sf.length-1);
                                var bf = sf.slice(sf.length-1,sf.length);
                                
                                
                                //DV.create(c.getWidth(), c.getHeight(), sd);
                                //c.removeAll();
                                //c.add(DV.chart);
                                DV.store.loadData(sd);
                                //DV.chart.redraw();
                            }
                        },
                        '->',
                        {
                            xtype: 'button',
                            text: 'Exit..',
                            handler: function() {
                                window.location.href = '../dhis-web-portal/redirect.action';
                            }
                        }
                    ]
                }
            ]
        })
    };    
});
