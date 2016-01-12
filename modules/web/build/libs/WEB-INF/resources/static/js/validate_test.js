$(document).ready(function() {
	var err = $("#error_message");

	var initInputCSS = {"border" : $("textarea").first().css("border")};
	var errorInputCSS = {"border" : "#ff0000 3px solid"};

	$("#submit_test_form").click(function() {
		var form = $(this).closest("form");

		var inputs = form.find("input, textarea").not(".unchanged");
		var invalid = false;
		inputs.each(function () {
			if (invalid) {
				return false;
			}

			if ($(this).val() == "") {
				err.html("Field must not be empty");
				invalid = true;
			} else if ($(this).hasClass("1")) {
				var intVal = parseInt($(this).val(), 10);
				if (isNaN(intVal)) {
					err.html("Field value must be integer");
					invalid = true;
				}
			} else if ($(this).hasClass("2")) {
				var floatVal = parseFloat($(this).val());
				if (isNaN(floatVal)) {
					err.html("Field value must be real number");
					invalid = true;
				}
			}

			if (invalid) {
				$(this).css(errorInputCSS);
				return false;
			} else {
				$(this).css(initInputCSS);
			}
		});

		if (!invalid) {
			form.submit();
		}
	});
});