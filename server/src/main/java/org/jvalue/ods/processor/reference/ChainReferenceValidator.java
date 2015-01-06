package org.jvalue.ods.processor.reference;


import com.google.inject.Inject;

import org.apache.commons.lang3.ClassUtils;
import org.jvalue.ods.api.processors.ProcessorReference;
import org.jvalue.ods.api.processors.ProcessorReferenceChain;
import org.jvalue.ods.processor.specification.ProcessorType;
import org.jvalue.ods.processor.specification.Specification;
import org.jvalue.ods.processor.specification.SpecificationManager;

import java.util.List;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ChainReferenceValidator implements ConstraintValidator<ValidChainReference, ProcessorReferenceChain> {

	private final SpecificationManager specificationManager;

	@Inject
	ChainReferenceValidator(SpecificationManager specificationManager) {
		this.specificationManager = specificationManager;
	}


	@Override
	public void initialize(ValidChainReference validReference) {
	}

	@Override
	public boolean isValid(ProcessorReferenceChain chainReference, ConstraintValidatorContext context) {
		if (chainReference == null) return true;

		List<ProcessorReference> references = chainReference.getProcessors();
		if (references.isEmpty())
			return setAndReturnErrorMessage(context, "processor chain cannot be empty");
		if (!specificationManager.getByName(references.get(0).getName()).getType().equals(ProcessorType.SOURCE_ADAPTER))
			return setAndReturnErrorMessage(context, "processor chain must start with " + ProcessorType.SOURCE_ADAPTER);

		ProcessorReference lastReference =  null;
		for (ProcessorReference reference : references) {
			if (lastReference != null) {
				ProcessorType lastType = specificationManager.getByName(lastReference.getName()).getType();
				ProcessorType nextType = specificationManager.getByName(reference.getName()).getType();
				if (!lastType.isValidNextFilter(nextType)) return setAndReturnErrorMessage(context, lastType + " cannot be followed by " + nextType);
				if (!isValid(reference, context)) return false;
			}
			lastReference = reference;
		}
		return true;
	}


	private boolean isValid(ProcessorReference reference, ConstraintValidatorContext context) {
		Specification description = specificationManager.getByName(reference.getName());
		if (description == null) return setAndReturnErrorMessage(context, "no processor found for id \"" + reference.getName() + "\"");
		if (reference.getArguments().size() != description.getArgumentTypes().size()) return setAndReturnErrorMessage(context, "expected " + description.getArgumentTypes().size() + " arguments but got " + reference.getArguments().size());

		for (Map.Entry<String, Class<?>> entry : description.getArgumentTypes().entrySet()) {
			Object arg = reference.getArguments().get(entry.getKey());
			if (arg == null) return setAndReturnErrorMessage(context, "no argument named \"" + entry.getKey() + "\" found");
			if (!ClassUtils.isAssignable(entry.getValue(), arg.getClass(), true)) return setAndReturnErrorMessage(context, "invalid argument type for \"" + entry.getKey() + "\"");
		}
		return true;
	}


	private boolean setAndReturnErrorMessage(ConstraintValidatorContext context, String message) {
		System.out.println(message);
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
		return false;
	}

}