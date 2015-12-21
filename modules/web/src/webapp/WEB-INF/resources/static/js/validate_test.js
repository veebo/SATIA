$(document).ready(function() {
	var err = $("#error_message");

	var initInputCSS = {"border" : $("textarea").first().css("border")};
	var errorInputCSS = {"border" : "#ff0000 3 px solid"};

	$("#submit_test_form").click(function() {
		err.html("");
		var testEditForm = $(this).closest("form");
		
		var srcLang = testEditForm.find("select[name='test_sourcelang']").val();
		var trgLang = testEditForm.find("select[name='test_targetlang']").val();
		if (srcLang == trgLang) {
			err.html("Source and target languages must differ");
			return;
		}

		var changedInputs = testEditForm.find("input, textarea").not(".unchanged");
		changedInputs.each(function () {
			var invalid = false;

			if ($(this).val() == "") {
				err.html("Field must not be empty");
				ivalid = true;
			} else if ($(this).hasClass("1")) {
				var intVal = parseInt($(this).val(), 10);
				if (isNaN(intVal)) {
					err.html("Field value must be integer");
					ivalid = true;
				}
			} else if ($(this).hasClass("2")) {
				var floatVal = parseFloat($(this).val());
				if (isNaN(floatVal)) {
					err.html("Field value must be real number");
					ivalid = true;
				}
			}

			if (invalid) {
				$(this).css(errorInputCSS);
				return;
			} else {
				$(this).css(initInputCSS);
				testEditForm.submit();
			}
		});
	});
});