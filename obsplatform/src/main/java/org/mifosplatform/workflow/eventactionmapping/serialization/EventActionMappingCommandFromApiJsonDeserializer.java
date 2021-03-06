package org.mifosplatform.workflow.eventactionmapping.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Deserializer for code JSON to validate API request.
 */
@Component
public final class EventActionMappingCommandFromApiJsonDeserializer {

	/**
	 * The parameters supported for this command.
	 */
	private final Set<String> supportedParameters = new HashSet<String>(
			Arrays.asList("event", "action", "process", "orderBy", "prePost", "processParams"));
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public EventActionMappingCommandFromApiJsonDeserializer(
			final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	public void validateForCreate(final String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("eventactionmapping");

		final JsonElement element = fromApiJsonHelper.parse(json);

		final String event = fromApiJsonHelper.extractStringNamed("event", element);
		baseDataValidator.reset().parameter("event").value(event).notBlank();

		final String action = fromApiJsonHelper.extractStringNamed("action", element);
		baseDataValidator.reset().parameter("action").value(action).notBlank();

		final String process = fromApiJsonHelper.extractStringNamed("process", element);
		baseDataValidator.reset().parameter("process").value(process).notBlank();
		
		final Long orderBy = fromApiJsonHelper.extractLongNamed("orderBy", element);
		baseDataValidator.reset().parameter("orderBy").value(orderBy).notBlank();
		
		final Boolean prePost = fromApiJsonHelper.extractBooleanNamed("prePost", element);
		baseDataValidator.reset().parameter("prePost").value(prePost).notBlank();
		
		final String processParams = fromApiJsonHelper.extractStringNamed("processParams", element);
		baseDataValidator.reset().parameter("processParams").value(processParams).notBlank();

		throwExceptionIfValidationWarningsExist(dataValidationErrors);

	}

	public void validateForUpdate(final String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("eventactionmapping");

		final JsonElement element = fromApiJsonHelper.parse(json);

		final Long id = fromApiJsonHelper.extractLongNamed("id", element);
		baseDataValidator.reset().parameter("id").value(id).notBlank();

		final String event = fromApiJsonHelper.extractStringNamed("event", element);
		baseDataValidator.reset().parameter("event").value(event).notBlank();

		final String action = fromApiJsonHelper.extractStringNamed("action", element);
		baseDataValidator.reset().parameter("action").value(action).notBlank();

		final String process = fromApiJsonHelper.extractStringNamed("process", element);
		baseDataValidator.reset().parameter("process").value(process).notBlank();

		throwExceptionIfValidationWarningsExist(dataValidationErrors);

	}

	/*
	 * public void validateForUpdate(final String json) { if
	 * (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
	 * 
	 * final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	 * fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
	 * supportedParameters);
	 * 
	 * final List<ApiParameterError> dataValidationErrors = new
	 * ArrayList<ApiParameterError>(); final DataValidatorBuilder
	 * baseDataValidator = new
	 * DataValidatorBuilder(dataValidationErrors).resource("code");
	 * 
	 * final JsonElement element = fromApiJsonHelper.parse(json); if
	 * (fromApiJsonHelper.parameterExists("name", element)) { final String name
	 * = fromApiJsonHelper.extractStringNamed("name", element);
	 * baseDataValidator
	 * .reset().parameter("name").value(name).notBlank().notExceedingLengthOf
	 * (100); }
	 * 
	 * throwExceptionIfValidationWarningsExist(dataValidationErrors); }
	 */

	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}
}