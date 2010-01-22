package org.lamsfoundation.lams.author.events
{
	import flash.events.Event;
	
	public class DropEvent extends DragEvent
	{
		public static const DROP_EVENT:String = "dropEvent";
		
		
		public function DropEvent(type:String, event:DrageEvent, bubbles:Boolean=true, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			
			this.dragInitiator = event.dragInitiator;
			this.dragSource = event.dragSource;
			this.action = event.action;
			this.ctrlKey = event.ctrlKey;
			this.altKey = event.altKey;
			this.shiftKey = event.shiftKey;
		}
	}
}