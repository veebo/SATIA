$(document).ready(function () {
	$("input, textarea, select").not(".ignore_unchanged").addClass("unchanged");
	$("form").on("change", "input, textarea, select", function () {
		$(this).removeClass("unchanged");
		$(this).removeAttr("disabled");
	});
	$("form").submit(function () {
		$(this).find(".unchanged").attr('disabled', 'disabled');
	});
});