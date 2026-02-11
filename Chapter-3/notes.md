https://www.notion.so/Chapter-3-The-Activity-Lifecycle-3041feed638a8088a741eb3071d288af

Activity States and Lifecycle Callbacks

Every instance of Activity has a lifecycle.
During this lifecycle, an activity transitions between four states: 
Resumed, Started, Created, Nonexistent
For each transition there is an Activity function that notifies the activity of the change in state.

| State | In memory? | Visible to User | In Foreground |
| --- | --- | --- | --- |
| Nonexistent | NO | NO | NO |
| Created  | YES | NO | NO |
| Started | YES | YES/Partially | NO |
| Resumed  | YES | YES | YES |

Nonexistent

It represents an Activity that has not yet been launched yet or Activity that was destroyed (by the user completely killing the app)
Also referred to as Destroyed state.
There is no instance in memory.
There is no associated view for the user to see or interact with.

Created

It represents an Activity that has an instance in memory but whose View is not yet visible on the screen.
This state occurs in passing when the activity is first spinning up and reoccurs any time the view is fully out of view.

Started

It represents an Activity that has lost focus but whose view is visible or partially visible.
Eg: User launched a new dialog-themed or transparent activity on top of it.
** An activity could also be fully visible but no in foreground if the user is viewing two activities in multi-window mode(split-screen mode).

Resumed

It represents an Activity that is in memory, fully visible, and in the foreground.
It is the state of the activity the user is currently interacting with.

Sub-classes of Activity(class) can call lifecycle callbacks to get work done.
Eg: onCreate
** We never call lifecycle callbacks functions ourself, we simply override them in our activity subclass. Then Android calls the lifecycle callbacks at appropriate time to notify the activity that is state is changing.

** We can as a developer finish an activity programmatically by calling Activity.finish()

Rotating an Activity

Each time on rotation, the activity instance is completely destroyed, then new instance is created.
onPause() → onStop() → onDestroy() → onCreate() → onStart() → onResume()

Device Configuration change and Activity Lifecycle

Device configuration is set of characteristics that describe the current state of an individual device. Like screen orientation, screen density, screen size, keyboard type, dock mode, language and more.
Applications can provide alternative resources to match device configuration, therefore when runtime configuration change occurs, there may be resources that are better match for the new configuration, so Android destroys the Activity, looks for resources are best fit and then rebuilds a new instance of the activity with them.
