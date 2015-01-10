package angularBeans.extentions;

import angularBeans.Extention;
import angularBeans.NGExtention;

@NGExtention
public class TemplatesDirectives implements Extention {

	public String render() {

		return ("\n"
				+ "app.directive('uiTemplate', function() {"
				+ " return {"
				+ "compile: function(tElem,attrs) {"
				+ " return function(scope,elem,attrs) {"
				+ "localStorage.setItem(\"URL\",document.URL);"
				+ "window.location = attrs.uiTemplate;"
				+ "};}};});"

				+ "app.directive('uiInsert', function($compile) {"
				+ "return {compile: function(tElem,attrs) {"
				+ "return function(scope,elem,attrs,compile) {"
				+ "elem.html(\"\");"
				+ "var addon=localStorage.getItem(attrs.uiInsert);"
				+ "elem.append($compile(addon)(scope));"
				+ "window.history.pushState(\"\", \"\", localStorage.getItem(\"URL\"));"
				+ "};}};})" + ".directive('uiDefine', function() {"
				+ "	  return {" + "   compile: function(tElem,attrs) {"
				+ "tElem.attr('hidden', 'true');"
				+ "   return function(scope,elem,attrs) {"
				+ "localStorage.setItem(attrs.uiDefine,elem.html());"
				+ "};}};});");
	}

}
