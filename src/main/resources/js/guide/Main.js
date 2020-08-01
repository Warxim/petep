var titleElement = document.getElementById('title');
var guideElement = document.getElementById('guide');

function loadGuide(title, html) {
	titleElement.innerHTML = title;
	guideElement.innerHTML = html;
}