package com.warmice.android.videomessaging.data;

import android.content.Context;

public abstract class User {

	public String username;
	public int id;
	public String name;

	public User() {
	}

	public boolean isSignedIn() {
		return username != null;
	}

	public static User load(Context context, int id) {
		CurrentUser currentUser = CurrentUser.load(context);
		if(currentUser.id == id) {
			return currentUser;
		} else {
			return Contact.load(context, id);
		}
	}
	
	public String getName(){
		if (name.equals(" ")){
			return username;
		}
		
		return name;
	}
	
	public abstract void store(Context context);
}
