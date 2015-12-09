$(document).ready(function () {
	$("input, textarea, select").not(".ignore_unchanged").addClass("unchanged");
	$("form").on("change", "input, textarea, select", function () {
		$(this).removeClass("unchanged");
	});
	$("form").submit(function () {
		$(this).find(".unchanged").remove();
	});
});