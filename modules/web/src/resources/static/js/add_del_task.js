$(document).ready(function() {
	var lastAddedTaskIndx = 0;
	var addedTasksInput = $("#added_tasks_input");

	$("#add_task").click(function() {
		var table = $(this).closest("form").find("#tasks .table");
		var newRow = $("#add_task_row_to_clone").clone();
		newRow.attr("id", "");
		newRow.find(".phr1").attr("name", "add_task"+lastAddedTaskIndx+"_phrase1");
		newRow.find(".phr2").attr("name", "add_task"+lastAddedTaskIndx+"_phrase2");
		newRow.find(".gen").attr("name", "add_task"+lastAddedTaskIndx+"_gen");
		table.append(newRow);
		addedTasksInput.attr("value", lastAddedTaskIndx+1);
		++lastAddedTaskIndx;
	});

	$(".remove").click(function() {
		var row = $(this).closest(".row");
		if (row.hasClass("added")) {
			row.remove();
		}
		else {
			row.css({"display" : "none"});
			var id = row.find("textarea")[0].attr("name").split("_")[0].split("task")[0];
			if (id == "-1") {
				return;
			}
			row.find(".cell").remove();
			var input = row.add("textarea");
			input.attr("name", "del_task"+id);
			input.text("+");
		}
	});
});