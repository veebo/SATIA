$(document).ready(function() {

	$("#tasks").on("change", ".gen select", function() {
		var genId = $(this).find("option:selected").val();
		if (genId == "null") {
			genId = $("select[name='test_generator'] option:selected").attr("value");
		}
		var newGenFields = $("#gen_fields_to_clone .gen_fields." + genId).clone();
		var row = $(this).closest(".row");
		var taskPrefix = row.find(".phr1 textarea").attr("name").split("_phrase1")[0];
		newGenFields.find(".field_value_input").each(function () {
			var fieldId = $(this).attr("name");
			$(this).attr("name", taskPrefix + "_field" + fieldId);
		});
		row.find(".gen_fields").replaceWith(newGenFields);
	});

	$("#tasks").on("click", ".add_field_value", function() {
		var field = $(this).closest(".field");
		var fieldType = field.find(".field_type").text();
		var fieldId = field.find(".field_id").text();
		var taskPrefix = field.closest(".row").find(".phr1 textarea").attr("name").split("_phrase1")[0];
		var newFieldValue = $("#field_value_inputs_to_clone").find("." + fieldType).closest(".field_value").clone();
		newFieldValue.find(".field_value_input").attr("name", taskPrefix + "_field" + fieldId);
		$(this).before(newFieldValue);
	});

	$("#tasks").on("click", ".del_field_value", function() {
		var fieldValue = $(this).closest(".field_value");
		var input = fieldValue.find(".field_value_input");
		input.removeClass("unchanged");
		var existingFValuePrefix = "field_value_";
		if (input.attr("name").substring(0, existingFValuePrefix.length) == existingFValuePrefix) {
			input.attr("name", "del_" + input.attr("name"));
			fieldValue.css({"display" : "none"});
		}
		else {
			fieldValue.remove();
		}
	});
});