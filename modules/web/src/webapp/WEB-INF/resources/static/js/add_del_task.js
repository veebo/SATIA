var lastAddedTaskIndx = 0;
$(document).ready(function() {
	var addedTasksInput = $("#added_tasks_input");

	$("#add_task").click(function() {
		var table = $(this).closest("form").find("#tasks .table");
		var newRow = $("#add_task_row_to_clone").clone();
		newRow.attr("id", "");
		newRow.find(".phr1 textarea").attr("name", "add_task"+lastAddedTaskIndx+"_phrase1");
		newRow.find(".phr2 textarea").attr("name", "add_task"+lastAddedTaskIndx+"_phrase2");
		newRow.find(".gen select").attr("name", "add_task"+lastAddedTaskIndx+"_gen");
		table.append(newRow);
		addedTasksInput.attr("value", lastAddedTaskIndx+1);
		++lastAddedTaskIndx;
	});

	$("#tasks").on( "click", ".remove", function() {
		var row = $(this).closest(".row");
		if (row.hasClass("added")) {
			row.remove();
		}
		else {
			var id = row.find("textarea").attr("name").split("_")[0].split("task")[1];
			if (id == "-1") {
				return;
			}
			row.find(".cell").remove();
			var input = addedTasksInput.clone();
			input.attr("id","");
			input.attr("name", "del_task"+id);
			input.attr("value", "+");
			row.append(input);
			row.css({"display" : "none"});
		}
	});
});