$(document).ready(function () {
	$("input, textarea, select").addClass("unchanged");
	$("input, textarea, select").change(function () {
		$(this).removeClass("unchanged");
	});
	$("form").submit(function () {
		$(this).find(".unchanged").remove();
	});
});