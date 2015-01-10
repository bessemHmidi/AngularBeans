package angularBeans.validation;

import java.io.Serializable;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import angularBeans.Util;

@ApplicationScoped
public class BeanValidationProcessor implements Serializable {

	private Set<Class> validationAnnotations;

	private StringBuffer buffer = new StringBuffer();;

	public void processBeanValidationParsing(Method method) {

		String modelName = Util.obtainFieldNameFromAccessor(method.getName());
		Annotation[] scannedAnnotations = method.getAnnotations();

		buffer.append("\nif (modelName === '").append(modelName).append("') {");
		for (Annotation a : scannedAnnotations) {

			if (!validationAnnotations.contains(a.annotationType())) {
				continue;
			}

			String name = (a.annotationType().getName());

			switch (name) {
			case "javax.validation.constraints.NotNull":
				buffer.append("\nentries[e].setAttribute('required', 'true');");
				break;

			case "org.hibernate.validator.constraints.Email":
				buffer.append("\nentries[e].setAttribute('type', 'email');");
				break;

			case "javax.validation.constraints.Pattern":
				String regex = ((Pattern) a).regexp();
				buffer.append("\nentries[e].setAttribute('ng-pattern', '")
						.append(regex).append("');");
				break;

			case "javax.validation.constraints.Size":
				buffer.append("\nentries[e].setAttribute('required', 'true');");
				Size sizeAnno = (Size) a;

				int maxLenth = sizeAnno.max();
				int minLenth = sizeAnno.min();

				if (!(minLenth <= Integer.MIN_VALUE))
					buffer.append("\nentries[e].setAttribute('ng-minlength', '")
							.append(minLenth).append("');");

				if (!(maxLenth >= Integer.MAX_VALUE))
					buffer.append("\nentries[e].setAttribute('ng-maxlength', '")
							.append(maxLenth).append("');");

				break;

			case "javax.validation.constraints.DecimalMin":
				String dMin = ((DecimalMin) a).value();
				buffer.append("\nentries[e].setAttribute('min', '")
						.append(dMin).append("');");
				break;
				
			case "javax.validation.constraints.DecimalMax":
				String dMax = ((DecimalMax) a).value();
				buffer.append("\nentries[e].setAttribute('max', '")
						.append(dMax).append("');");
				break;
				
				
			case "javax.validation.constraints.Min":
				long min = ((Min) a).value();
				buffer.append("\nentries[e].setAttribute('min', '")
						.append(min).append("');");
				break;
				
			case "javax.validation.constraints.Max":
				long max = ((Max) a).value();
				buffer.append("\nentries[e].setAttribute('max', '")
						.append(max).append("');");
				break;
				
			default:
				break;
			}
		}

		buffer.append("\n}");
	}

	@PostConstruct
	public void init() {
		validationAnnotations = new HashSet<>();
		validationAnnotations.addAll(Arrays.asList(NotNull.class, Size.class,
				Pattern.class,DecimalMin.class,DecimalMax.class,Min.class,Max.class));

	}

	public void build(StringWriter writer) {

		writer.write("app");
		writer.write(".directive('beanValidate', function($compile) {");
		writer.write("\nreturn {");
		writer.write("\ncompile : function(tElem, attrs) {");
		writer.write("\nreturn function(scope, elem, attrs, compile) {");
		writer.write("\nvar getAllInputModel = function(attribute) {");
		writer.write("\nvar matchingElements = [];");
		writer.write("\nvar allElements = document.getElementsByTagName('input');");
		writer.write("\nfor (var i = 0, n = allElements.length; i < n; i++) {");
		writer.write("\nif (allElements[i].getAttribute(attribute)) {");
		writer.write("\nmatchingElements.push(allElements[i]);");
		writer.write("\n}}");
		writer.write("\nreturn matchingElements;}");
		writer.write("\nvar entries = (getAllInputModel('ng-model'));");
		writer.write("\nfor (e in entries) {");
		writer.write("\nvar modelName = (entries[e].getAttribute('ng-model'));");

		String result = buffer.toString();
		writer.write(result);
		buffer = new StringBuffer();

		writer.write("\n};");
		writer.write("\nvar old = \"<!-- validation -->\" + elem.html();");
		writer.write("\nelem.html('');");
		writer.write("\nelem.append($compile(old)(scope));");
		writer.write("\n;};}};});");

	}

}
