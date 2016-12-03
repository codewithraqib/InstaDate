package com.example.raqib.instadate;


import android.widget.TextView;

/*
 * Data object that holds all of our information about a StackExchange Site.
 */
public class NewsItems {

	private String name;
	private String link;
	private String about;
	private String imgUrl;
	private String date;

	public TextView setTitle(String name) {
		this.name = name;
		return null;
	}
	public String getTitle() {
		return name;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDate() {
		return date;
	}

	public void setLink(String link) {
		this.link = link;
	}
	public String getLink() {
		return link;
	}

	public void setDescription(String about) {
		this.about = about;
	}
	public String getDescription() {
		return about;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	@Override
	public String toString() {
		return "StackSite [name=" + name + ", link=" + link + ", about="
				+ about + ", imgUrl=" + imgUrl + "]";
	}
}
