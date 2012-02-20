// based on
// http://www.bennadel.com/blog/2000-Powering-Publish-And-Subscribe-Functionality-With-Native-jQuery-Event-Management.htm

function EventManager() {
	this.eventDom = $(document.createElement('eventDom'));
	this.eventDom.data('preTrigger', {});
}

EventManager.prototype.bind = function(eventType, callback) {
	// check if this event type has a pre-trigger interceptor, if not add it
	if (!this.eventDom.data('preTrigger')[eventType]) {
		this.eventDom.bind(eventType, jQuery.proxy(this.preTrigger, this));
		this.eventDom.data('preTrigger')[eventType] = true;
	}
	
	// replace the callback with one that will execute in the EventManager context
	arguments[arguments.length-1] = jQuery.proxy(
		arguments[arguments.length-1], this
	);
	
	// bind the event to eventDom
	jQuery.fn.bind.apply(this.eventDom, arguments);
	
	return (this);
};

EventManager.prototype.unbind = function(eventType, callback) {
	this.eventDom.unbind(eventType, callback);
	return (this);
};

EventManager.prototype.trigger = function(eventType, data) {
	this.eventDom.trigger(eventType, data);
	return (this);
};

EventManager.prototype.preTrigger = function(event) {
	// changes event target to EventManager, instead of eventDom
	event.target = this;
};

eventManager = new EventManager();