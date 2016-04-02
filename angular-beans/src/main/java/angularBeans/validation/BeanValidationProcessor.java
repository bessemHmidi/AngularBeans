/*
 * AngularBeans, CDI-AngularJS bridge 
 *
 * Copyright (c) 2014, Bessem Hmidi. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 */
/**
 * @author Bessem Hmidi
 */
package angularBeans.validation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import angularBeans.util.AngularBeansUtils;
import angularBeans.util.ClosureCompiler;
import angularBeans.util.CommonUtils;
import angularBeans.js.StaticJsCache;

/**
 * The bean validation processor generates the HTML5 validation attributes from
 * Bean Validation annotations.
 *
 * @author Bessem Hmidi
 *
 */
@SuppressWarnings("serial")
@ApplicationScoped
public class BeanValidationProcessor implements Serializable {

	private Set<Class> validationAnnotations;

	@Inject
	AngularBeansUtils util;

	private final StringBuffer buffer = new StringBuffer();

	public void processBeanValidationParsing(Method method) {

		String modelName = CommonUtils.obtainFieldNameFromAccessor(method
				.getName());
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
					Size sizeConstraint = (Size) a;

					int maxLength = sizeConstraint.max();
					int minLength = sizeConstraint.min();

					if (minLength < 0) {
						throw new IllegalArgumentException("The min parameter cannot be negative.");
					}

					if (maxLength < 0) {
						throw new IllegalArgumentException("The max parameter cannot be negative.");
					}

					if (minLength > 0) {
						buffer.append("\nentries[e].setAttribute('ng-minlength', '")
								.append(minLength).append("');");
					}

					if (maxLength < Integer.MAX_VALUE) {
						buffer.append("\nentries[e].setAttribute('ng-maxlength', '")
								.append(maxLength).append("');");
					}

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
					buffer.append("\nentries[e].setAttribute('min', '").append(min)
							.append("');");
					break;

				case "javax.validation.constraints.Max":
					long max = ((Max) a).value();
					buffer.append("\nentries[e].setAttribute('max', '").append(max)
							.append("');");
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
				Pattern.class, DecimalMin.class, DecimalMax.class, Min.class,
				Max.class));

	}

	public void build() {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("app");
		stringBuilder.append(".directive('beanValidate', function($compile) {");
		stringBuilder.append("\nreturn {");
		stringBuilder.append("\ncompile : function(tElem, attrs) {");
		stringBuilder
				.append("\nreturn function(scope, elem, attrs, compile) {");
		stringBuilder.append("\nvar getAllInputModel = function(attribute) {");
		stringBuilder.append("\nvar matchingElements = [];");
		stringBuilder
				.append("\nvar allElements = document.getElementsByTagName('input');");
		stringBuilder
				.append("\nfor (var i = 0, n = allElements.length; i < n; i++) {");
		stringBuilder.append("\nif (allElements[i].getAttribute(attribute)) {");
		stringBuilder.append("\nmatchingElements.push(allElements[i]);");
		stringBuilder.append("\n}}");
		stringBuilder.append("\nreturn matchingElements;}");
		stringBuilder.append("\nvar entries = (getAllInputModel('name'));");
		stringBuilder.append("\nfor (e in entries) {");
		stringBuilder
				.append("\nvar modelName = (entries[e].getAttribute('name'));");

		stringBuilder.append(buffer);

		stringBuilder.append("\n};");
		stringBuilder
				.append("\nvar old = \"<!-- validation -->\" + elem.html();");
		stringBuilder.append("\nelem.html('');");
		stringBuilder.append("\nelem.append($compile(old)(scope));");
		stringBuilder.append("\n;};}};});");

		StaticJsCache.VALIDATION_SCRIPT.append(new ClosureCompiler().getCompressedJavaScript(stringBuilder.toString()));
	}

}
