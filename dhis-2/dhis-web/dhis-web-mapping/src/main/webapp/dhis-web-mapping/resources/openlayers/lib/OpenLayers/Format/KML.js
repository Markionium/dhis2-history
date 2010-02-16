OpenLayers.Format.KML=OpenLayers.Class(OpenLayers.Format.XML,{kmlns:"http://earth.google.com/kml/2.0",placemarksDesc:"No description available",foldersName:"OpenLayers export",foldersDesc:"Exported on "+new Date(),extractAttributes:true,extractStyles:false,internalns:null,features:null,styles:null,styleBaseUrl:"",fetched:null,maxDepth:0,initialize:function(options){this.regExes={trimSpace:(/^\s*|\s*$/g),removeSpace:(/\s*/g),splitSpace:(/\s+/),trimComma:(/\s*,\s*/g),kmlColor:(/(\w{2})(\w{2})(\w{2})(\w{2})/),kmlIconPalette:(/root:\/\/icons\/palette-(\d+)(\.\w+)/),straightBracket:(/\$\[(.*?)\]/g)};OpenLayers.Format.XML.prototype.initialize.apply(this,[options]);},read:function(data){this.features=[];this.styles={};this.fetched={};var options={depth:0,styleBaseUrl:this.styleBaseUrl};return this.parseData(data,options);},parseData:function(data,options){if(typeof data=="string"){data=OpenLayers.Format.XML.prototype.read.apply(this,[data]);}
var types=["Link","NetworkLink","Style","StyleMap","Placemark"];for(var i=0,len=types.length;i<len;++i){var type=types[i];var nodes=this.getElementsByTagNameNS(data,"*",type);if(nodes.length==0){continue;}
switch(type.toLowerCase()){case"link":case"networklink":this.parseLinks(nodes,options);break;case"style":if(this.extractStyles){this.parseStyles(nodes,options);}
break;case"stylemap":if(this.extractStyles){this.parseStyleMaps(nodes,options);}
break;case"placemark":this.parseFeatures(nodes,options);break;}}
return this.features;},parseLinks:function(nodes,options){if(options.depth>=this.maxDepth){return false;}
var newOptions=OpenLayers.Util.extend({},options);newOptions.depth++;for(var i=0,len=nodes.length;i<len;i++){var href=this.parseProperty(nodes[i],"*","href");if(href&&!this.fetched[href]){this.fetched[href]=true;var data=this.fetchLink(href);if(data){this.parseData(data,newOptions);}}}},fetchLink:function(href){var request=OpenLayers.Request.GET({url:href,async:false});if(request){return request.responseText;}},parseStyles:function(nodes,options){for(var i=0,len=nodes.length;i<len;i++){var style=this.parseStyle(nodes[i]);if(style){styleName=(options.styleBaseUrl||"")+"#"+style.id;this.styles[styleName]=style;}}},parseStyle:function(node){var style={};var types=["LineStyle","PolyStyle","IconStyle","BalloonStyle"];var type,nodeList,geometry,parser;for(var i=0,len=types.length;i<len;++i){type=types[i];styleTypeNode=this.getElementsByTagNameNS(node,"*",type)[0];if(!styleTypeNode){continue;}
switch(type.toLowerCase()){case"linestyle":var color=this.parseProperty(styleTypeNode,"*","color");if(color){var matches=(color.toString()).match(this.regExes.kmlColor);var alpha=matches[1];style["strokeOpacity"]=parseInt(alpha,16)/255;var b=matches[2];var g=matches[3];var r=matches[4];style["strokeColor"]="#"+r+g+b;}
var width=this.parseProperty(styleTypeNode,"*","width");if(width){style["strokeWidth"]=width;}
case"polystyle":var color=this.parseProperty(styleTypeNode,"*","color");if(color){var matches=(color.toString()).match(this.regExes.kmlColor);var alpha=matches[1];style["fillOpacity"]=parseInt(alpha,16)/255;var b=matches[2];var g=matches[3];var r=matches[4];style["fillColor"]="#"+r+g+b;}
var fill=this.parseProperty(styleTypeNode,"*","fill");if(fill=="0"){style["fillColor"]="none";}
break;case"iconstyle":var scale=parseFloat(this.parseProperty(styleTypeNode,"*","scale")||1);var width=32*scale;var height=32*scale;var iconNode=this.getElementsByTagNameNS(styleTypeNode,"*","Icon")[0];if(iconNode){var href=this.parseProperty(iconNode,"*","href");if(href){var w=this.parseProperty(iconNode,"*","w");var h=this.parseProperty(iconNode,"*","h");var google="http://maps.google.com/mapfiles/kml";if(OpenLayers.String.startsWith(href,google)&&!w&&!h){w=64;h=64;scale=scale/2;}
w=w||h;h=h||w;if(w){width=parseInt(w)*scale;}
if(h){height=parseInt(h)*scale;}
var matches=href.match(this.regExes.kmlIconPalette);if(matches){var palette=matches[1];var file_extension=matches[2];var x=this.parseProperty(iconNode,"*","x");var y=this.parseProperty(iconNode,"*","y");var posX=x?x/32:0;var posY=y?(7-y/32):7;var pos=posY*8+posX;href="http://maps.google.com/mapfiles/kml/pal"
+palette+"/icon"+pos+file_extension;}
style["graphicOpacity"]=1;style["externalGraphic"]=href;}}
var hotSpotNode=this.getElementsByTagNameNS(styleTypeNode,"*","hotSpot")[0];if(hotSpotNode){var x=parseFloat(hotSpotNode.getAttribute("x"));var y=parseFloat(hotSpotNode.getAttribute("y"));var xUnits=hotSpotNode.getAttribute("xunits");if(xUnits=="pixels"){style["graphicXOffset"]=-x*scale;}
else if(xUnits=="insetPixels"){style["graphicXOffset"]=-width+(x*scale);}
else if(xUnits=="fraction"){style["graphicXOffset"]=-width*x;}
var yUnits=hotSpotNode.getAttribute("yunits");if(yUnits=="pixels"){style["graphicYOffset"]=-height+(y*scale)+1;}
else if(yUnits=="insetPixels"){style["graphicYOffset"]=-(y*scale)+1;}
else if(yUnits=="fraction"){style["graphicYOffset"]=-height*(1-y)+1;}}
style["graphicWidth"]=width;style["graphicHeight"]=height;break;case"balloonstyle":var balloonStyle=OpenLayers.Util.getXmlNodeValue(styleTypeNode);if(balloonStyle){style["balloonStyle"]=balloonStyle.replace(this.regExes.straightBracket,"${$1}");}
break;default:}}
if(!style["strokeColor"]&&style["fillColor"]){style["strokeColor"]=style["fillColor"];}
var id=node.getAttribute("id");if(id&&style){style.id=id;}
return style;},parseStyleMaps:function(nodes,options){for(var i=0,len=nodes.length;i<len;i++){var node=nodes[i];var pairs=this.getElementsByTagNameNS(node,"*","Pair");var id=node.getAttribute("id");for(var j=0,jlen=pairs.length;j<jlen;j++){var pair=pairs[j];var key=this.parseProperty(pair,"*","key");var styleUrl=this.parseProperty(pair,"*","styleUrl");if(styleUrl&&key=="normal"){this.styles[(options.styleBaseUrl||"")+"#"+id]=this.styles[(options.styleBaseUrl||"")+styleUrl];}
if(styleUrl&&key=="highlight"){}}}},parseFeatures:function(nodes,options){var features=new Array(nodes.length);for(var i=0,len=nodes.length;i<len;i++){var featureNode=nodes[i];var feature=this.parseFeature.apply(this,[featureNode]);if(feature){if(this.extractStyles&&feature.attributes&&feature.attributes.styleUrl){feature.style=this.getStyle(feature.attributes.styleUrl,options);}
if(this.extractStyles){var inlineStyleNode=this.getElementsByTagNameNS(featureNode,"*","Style")[0];if(inlineStyleNode){var inlineStyle=this.parseStyle(inlineStyleNode);if(inlineStyle){feature.style=OpenLayers.Util.extend(feature.style,inlineStyle);}}}
features[i]=feature;}else{throw"Bad Placemark: "+i;}}
this.features=this.features.concat(features);},parseFeature:function(node){var order=["MultiGeometry","Polygon","LineString","Point"];var type,nodeList,geometry,parser;for(var i=0,len=order.length;i<len;++i){type=order[i];this.internalns=node.namespaceURI?node.namespaceURI:this.kmlns;nodeList=this.getElementsByTagNameNS(node,this.internalns,type);if(nodeList.length>0){var parser=this.parseGeometry[type.toLowerCase()];if(parser){geometry=parser.apply(this,[nodeList[0]]);if(this.internalProjection&&this.externalProjection){geometry.transform(this.externalProjection,this.internalProjection);}}else{OpenLayers.Console.error(OpenLayers.i18n("unsupportedGeometryType",{'geomType':type}));}
break;}}
var attributes;if(this.extractAttributes){attributes=this.parseAttributes(node);}
var feature=new OpenLayers.Feature.Vector(geometry,attributes);var fid=node.getAttribute("id")||node.getAttribute("name");if(fid!=null){feature.fid=fid;}
return feature;},getStyle:function(styleUrl,options){var styleBaseUrl=OpenLayers.Util.removeTail(styleUrl);var newOptions=OpenLayers.Util.extend({},options);newOptions.depth++;newOptions.styleBaseUrl=styleBaseUrl;if(!this.styles[styleUrl]&&!OpenLayers.String.startsWith(styleUrl,"#")&&newOptions.depth<=this.maxDepth&&!this.fetched[styleBaseUrl]){var data=this.fetchLink(styleBaseUrl);if(data){this.parseData(data,newOptions);}}
var style=OpenLayers.Util.extend({},this.styles[styleUrl]);return style;},parseGeometry:{point:function(node){var nodeList=this.getElementsByTagNameNS(node,this.internalns,"coordinates");var coords=[];if(nodeList.length>0){var coordString=nodeList[0].firstChild.nodeValue;coordString=coordString.replace(this.regExes.removeSpace,"");coords=coordString.split(",");}
var point=null;if(coords.length>1){if(coords.length==2){coords[2]=null;}
point=new OpenLayers.Geometry.Point(coords[0],coords[1],coords[2]);}else{throw"Bad coordinate string: "+coordString;}
return point;},linestring:function(node,ring){var nodeList=this.getElementsByTagNameNS(node,this.internalns,"coordinates");var line=null;if(nodeList.length>0){var coordString=this.getChildValue(nodeList[0]);coordString=coordString.replace(this.regExes.trimSpace,"");coordString=coordString.replace(this.regExes.trimComma,",");var pointList=coordString.split(this.regExes.splitSpace);var numPoints=pointList.length;var points=new Array(numPoints);var coords,numCoords;for(var i=0;i<numPoints;++i){coords=pointList[i].split(",");numCoords=coords.length;if(numCoords>1){if(coords.length==2){coords[2]=null;}
points[i]=new OpenLayers.Geometry.Point(coords[0],coords[1],coords[2]);}else{throw"Bad LineString point coordinates: "+
pointList[i];}}
if(numPoints){if(ring){line=new OpenLayers.Geometry.LinearRing(points);}else{line=new OpenLayers.Geometry.LineString(points);}}else{throw"Bad LineString coordinates: "+coordString;}}
return line;},polygon:function(node){var nodeList=this.getElementsByTagNameNS(node,this.internalns,"LinearRing");var numRings=nodeList.length;var components=new Array(numRings);if(numRings>0){var ring;for(var i=0,len=nodeList.length;i<len;++i){ring=this.parseGeometry.linestring.apply(this,[nodeList[i],true]);if(ring){components[i]=ring;}else{throw"Bad LinearRing geometry: "+i;}}}
return new OpenLayers.Geometry.Polygon(components);},multigeometry:function(node){var child,parser;var parts=[];var children=node.childNodes;for(var i=0,len=children.length;i<len;++i){child=children[i];if(child.nodeType==1){var type=(child.prefix)?child.nodeName.split(":")[1]:child.nodeName;var parser=this.parseGeometry[type.toLowerCase()];if(parser){parts.push(parser.apply(this,[child]));}}}
return new OpenLayers.Geometry.Collection(parts);}},parseAttributes:function(node){var attributes={};var edNodes=node.getElementsByTagName("ExtendedData");if(edNodes.length){attributes=this.parseExtendedData(edNodes[0]);}
var child,grandchildren,grandchild;var children=node.childNodes;for(var i=0,len=children.length;i<len;++i){child=children[i];if(child.nodeType==1){grandchildren=child.childNodes;if(grandchildren.length==1||grandchildren.length==3){var grandchild;switch(grandchildren.length){case 1:grandchild=grandchildren[0];break;case 3:default:grandchild=grandchildren[1];break;}
if(grandchild.nodeType==3||grandchild.nodeType==4){var name=(child.prefix)?child.nodeName.split(":")[1]:child.nodeName;var value=OpenLayers.Util.getXmlNodeValue(grandchild);if(value){value=value.replace(this.regExes.trimSpace,"");attributes[name]=value;}}}}}
return attributes;},parseExtendedData:function(node){var attributes={};var dataNodes=node.getElementsByTagName("Data");for(var i=0,len=dataNodes.length;i<len;i++){var data=dataNodes[i];var key=data.getAttribute("name");var ed={};var valueNode=data.getElementsByTagName("value");if(valueNode.length){ed['value']=this.getChildValue(valueNode[0]);}
var nameNode=data.getElementsByTagName("displayName");if(nameNode.length){ed['displayName']=this.getChildValue(nameNode[0]);}
attributes[key]=ed;}
return attributes;},parseProperty:function(xmlNode,namespace,tagName){var value;var nodeList=this.getElementsByTagNameNS(xmlNode,namespace,tagName);try{value=OpenLayers.Util.getXmlNodeValue(nodeList[0]);}catch(e){value=null;}
return value;},write:function(features){if(!(features instanceof Array)){features=[features];}
var kml=this.createElementNS(this.kmlns,"kml");var folder=this.createFolderXML();for(var i=0,len=features.length;i<len;++i){folder.appendChild(this.createPlacemarkXML(features[i]));}
kml.appendChild(folder);return OpenLayers.Format.XML.prototype.write.apply(this,[kml]);},createFolderXML:function(){var folderName=this.createElementNS(this.kmlns,"name");var folderNameText=this.createTextNode(this.foldersName);folderName.appendChild(folderNameText);var folderDesc=this.createElementNS(this.kmlns,"description");var folderDescText=this.createTextNode(this.foldersDesc);folderDesc.appendChild(folderDescText);var folder=this.createElementNS(this.kmlns,"Folder");folder.appendChild(folderName);folder.appendChild(folderDesc);return folder;},createPlacemarkXML:function(feature){var placemarkName=this.createElementNS(this.kmlns,"name");var name=(feature.attributes.name)?feature.attributes.name:feature.id;placemarkName.appendChild(this.createTextNode(name));var placemarkDesc=this.createElementNS(this.kmlns,"description");var desc=(feature.attributes.description)?feature.attributes.description:this.placemarksDesc;placemarkDesc.appendChild(this.createTextNode(desc));var placemarkNode=this.createElementNS(this.kmlns,"Placemark");if(feature.fid!=null){placemarkNode.setAttribute("id",feature.fid);}
placemarkNode.appendChild(placemarkName);placemarkNode.appendChild(placemarkDesc);var geometryNode=this.buildGeometryNode(feature.geometry);placemarkNode.appendChild(geometryNode);return placemarkNode;},buildGeometryNode:function(geometry){if(this.internalProjection&&this.externalProjection){geometry=geometry.clone();geometry.transform(this.internalProjection,this.externalProjection);}
var className=geometry.CLASS_NAME;var type=className.substring(className.lastIndexOf(".")+1);var builder=this.buildGeometry[type.toLowerCase()];var node=null;if(builder){node=builder.apply(this,[geometry]);}
return node;},buildGeometry:{point:function(geometry){var kml=this.createElementNS(this.kmlns,"Point");kml.appendChild(this.buildCoordinatesNode(geometry));return kml;},multipoint:function(geometry){return this.buildGeometry.collection.apply(this,[geometry]);},linestring:function(geometry){var kml=this.createElementNS(this.kmlns,"LineString");kml.appendChild(this.buildCoordinatesNode(geometry));return kml;},multilinestring:function(geometry){return this.buildGeometry.collection.apply(this,[geometry]);},linearring:function(geometry){var kml=this.createElementNS(this.kmlns,"LinearRing");kml.appendChild(this.buildCoordinatesNode(geometry));return kml;},polygon:function(geometry){var kml=this.createElementNS(this.kmlns,"Polygon");var rings=geometry.components;var ringMember,ringGeom,type;for(var i=0,len=rings.length;i<len;++i){type=(i==0)?"outerBoundaryIs":"innerBoundaryIs";ringMember=this.createElementNS(this.kmlns,type);ringGeom=this.buildGeometry.linearring.apply(this,[rings[i]]);ringMember.appendChild(ringGeom);kml.appendChild(ringMember);}
return kml;},multipolygon:function(geometry){return this.buildGeometry.collection.apply(this,[geometry]);},collection:function(geometry){var kml=this.createElementNS(this.kmlns,"MultiGeometry");var child;for(var i=0,len=geometry.components.length;i<len;++i){child=this.buildGeometryNode.apply(this,[geometry.components[i]]);if(child){kml.appendChild(child);}}
return kml;}},buildCoordinatesNode:function(geometry){var coordinatesNode=this.createElementNS(this.kmlns,"coordinates");var path;var points=geometry.components;if(points){var point;var numPoints=points.length;var parts=new Array(numPoints);for(var i=0;i<numPoints;++i){point=points[i];parts[i]=point.x+","+point.y;}
path=parts.join(" ");}else{path=geometry.x+","+geometry.y;}
var txtNode=this.createTextNode(path);coordinatesNode.appendChild(txtNode);return coordinatesNode;},CLASS_NAME:"OpenLayers.Format.KML"});