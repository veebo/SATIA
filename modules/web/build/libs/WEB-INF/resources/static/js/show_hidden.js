function replace_substring(elem, str1, str2) {
	var text = elem.text();
	var indx = text.indexOf(str1);
	if (indx <= -1) {
		return false;
	}
	var replaced = text.substring(0,indx) + str2 + text.substring(indx+str1.length);
	elem.text(replaced);
	return true;
}
$(document).ready(function () {
	$('.show_hidden').click(function() {
		$(this).parent().find('.hidden').toggle();
		if (!replace_substring($(this),'show','hide')) {
			replace_substring($(this),'hide', 'show');
		}
	});
});