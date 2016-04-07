#appfour Android Wear open input method API

Our apps for Android Wear offer an open API to integrate custom keyboard apps or other apps implementing alternative input methods. Keyboards which are already implementing the API are:

* [TouchOne](https://play.google.com/store/apps/details?id=net.infiniti.touchone.touchone)
* [FlickKey](http://flickkey.com/Link2FlickKeyForWearApp.html)

![TouchOne](https://github.com/appfour/AndroidWearKeyboardApi/blob/master/graphics/touchone.jpg)
![FlickKey](https://github.com/appfour/AndroidWearKeyboardApi/blob/master/graphics/flickkey.png)

As soon as a keyboard app implements this API and is installed on the wearable device all appfour wear apps will use this keyboard app by default. Currently, these apps are:

*	[Messages for Android Wear](https://play.google.com/store/apps/details?id=com.appfour.wearmessages)
*	[Web Browser for Android Wear](https://play.google.com/store/apps/details?id=com.appfour.wearbrowser)
*	[Gmail Client for Android Wear](https://play.google.com/store/apps/details?id=com.appfour.wearmail)
*	[Calendar for Android Wear](https://play.google.com/store/apps/details?id=com.appfour.wearcalendar)
*	[Video/YouTube for Android Wear](https://play.google.com/store/apps/details?id=com.appfour.weartube)
*	[Photo Gallery for Android Wear](https://play.google.com/store/apps/details?id=com.appfour.wearphotos)
*	[Documents for Android Wear](https://play.google.com/store/apps/details?id=com.appfour.weardocuments)

![Messages for Android Wear](https://github.com/appfour/AndroidWearKeyboardApi/blob/master/graphics/ic_launcher_messages.png)
![Web Browser for Android Wear](https://github.com/appfour/AndroidWearKeyboardApi/blob/master/graphics/ic_launcher_wib.png)
![Gmail Client for Android Wear](https://github.com/appfour/AndroidWearKeyboardApi/blob/master/graphics/ic_launcher_mail.png)
![Calendar for Android Wear](https://github.com/appfour/AndroidWearKeyboardApi/blob/master/graphics/ic_launcher_calendar.png)
![Video/YouTube for Android Wear](https://github.com/appfour/AndroidWearKeyboardApi/blob/master/graphics/ic_launcher_youtube.png)
![Photo Gallery for Android Wear](https://github.com/appfour/AndroidWearKeyboardApi/blob/master/graphics/ic_launcher_photos.png)
![Documents for Android Wear](https://github.com/appfour/AndroidWearKeyboardApi/blob/master/graphics/ic_launcher_documents.png)


##Implementing the API

We provide a very basic [sample implementation](https://github.com/appfour/AndroidWearKeyboardApi/blob/master/WearKeyboard/wear/src/main/java/com/appfour/wearkeyboard/DemoKeyboardActivity.java) of the API. Implementation is straight forward:

###1. Declare a keyboard activity

In the AndroidManifest.xml of your wearable (!) app declare an activity implementing the keyboard. This can be a full screen activity handling the input UI or a transparent activity if the keyboard only covers part of the screen.

```XML
<activity
    android:name=".DemoKeyboardActivity"
    android:theme="@android:style/Theme.Translucent.NoTitleBar.Fullscreen">
    <intent-filter>
        <action android:name="appfour.intent.action.REQUEST_INPUT"/>
        <category android:name="android.intent.category.DEFAULT"/>
    </intent-filter>
</activity>
```

###2. Get data from the calling app

In the activity implementation get the data from the calling app. The calling app sends the original edited text, current selection, input type, action and hint text: 

```Java
// Get text from calling app
final String callingPackageName = getCallingPackage();
final String token = getIntent().getStringExtra("Token");
String text = getIntent().getStringExtra("Text");
int selectionStart = getIntent().getIntExtra("SelectionStart", 0);
int selectionEnd = getIntent().getIntExtra("SelectionEnd", 0);
String hintText = getIntent().getStringExtra("HintText");
int inputType = getIntent().getIntExtra("InputType", 0);
int action = getIntent().getIntExtra("Action", EditorInfo.IME_ACTION_NONE);
```

The data contains the edited text and current selection, as well as the hint text of the edited EditText in the calling app.
The inputType extra is a combination of `android.text.InputType` constants. Currently `InputType.TYPE_CLASS_TEXT`, `InputType.TYPE_TEXT_VARIATION_URI` and `InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS` are used.

The action extra is a combination of android.view.inputmethod.EditorInfo constants. Currently `EditorInfo.IME_ACTION_NONE`, `EditorInfo.IME_ACTION_GO`, `EditorInfo.IME_ACTION_SEARCH`, `EditorInfo.IME_ACTION_SEND` and `EditorInfo.IME_ACTION_NEXT` are used.

The token extra is to make sure that no app we haven't called can send texts to our apps. It is passed by the calling intent and verified when returned. So just pass it along when sending back a broadcast.

###3. Send text updates when the user types

Finally return the edited text and new selection to the calling app. This can be done incrementally while typing (useful for transparent activities) or when the user finishes the text input:

```Java
// Send typed text
Intent data = new Intent("appfour.intent.action.UPDATE_INPUT");
data.setPackage(callingPackageName);
data.putExtra("Token", token);
data.putExtra("Text", typedText);
data.putExtra("SelectionStart", typedText.length());
data.putExtra("SelectionEnd", typedText.length());
sendBroadcast(data);
```

###4. Optionally perform the keyboard action

When the action extra specifies an action other than `IME_ACTION_NONE`, you can optionally provide a way to invoke that action. For example, the messages app passes `IME_ACTION_SEND` for new messages. Your keyboard can then contain a “Send” button that performs the action directly, so the user doesn’t have to press the send button of the calling app. To perform the action, you have to pass the extra PerformAction when finishing the keyboard activity:

```Java
Intent data = new Intent();
data.putExtra("PerformAction", true);
setResult(RESULT_OK, data);
finish();
```
