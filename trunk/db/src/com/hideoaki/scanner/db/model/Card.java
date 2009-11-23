package com.hideoaki.scanner.db.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import com.hideoaki.scanner.db.utils.ScannerDBException;

import au.com.bytecode.opencsv.CSVReader;

public class Card {
	public static final String DEFAULT_LOCAL_CARD_FILE = "defaultcard.csv";

	private String firstName;
	private String lastName;
	private String position;
	private String email;
	private String company;
	private String website;
	private String address;
	private String city;
	private String state;
	private String country;
	private String zip;
	private String telephone;
	private String fax;
	private String mobile;
	private String note;
	private String imgFront;
	private String imgBack;
	private Group group;
	private int privacy;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getImgFront() {
		return imgFront;
	}

	public void setImgFront(String imgFront) {
		this.imgFront = imgFront;
	}

	public String getImgBack() {
		return imgBack;
	}

	public void setImgBack(String imgBack) {
		this.imgBack = imgBack;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public int getPrivacy() {
		return privacy;
	}

	public void setPrivacy(int privacy) {
		this.privacy = privacy;
	}

	public static ArrayList<Card> loadLocalCard(String pathToCSV)
			throws ScannerDBException {
		try {
			ArrayList<Card> listCard = new ArrayList<Card>();
			CSVReader reader = new CSVReader(new FileReader(pathToCSV));
			String[] nextLine;
			Card card = new Card();
			while ((nextLine = reader.readNext()) != null) {
				card = new Card();
				card. firstName = nextLine[0];
				card. lastName= nextLine[1];
				card. position= nextLine[2];
				card. email= nextLine[3];
				card. company= nextLine[4];
				card. website= nextLine[5];
				card. address= nextLine[6];
				card. city= nextLine[7];
				card. state= nextLine[8];
				card. country= nextLine[9];
				card. zip= nextLine[10];
				card. telephone= nextLine[11];
				card. fax= nextLine[12];
				card. mobile= nextLine[13];
				card. note= nextLine[14];
				card. imgFront= nextLine[15];
				card. imgBack= nextLine[16];
				card.group= new Group( nextLine[17]);
				card.privacy= Integer.parseInt(nextLine[18]) ;
				System.out.println("Name: [" + nextLine[0] + "]\nAddress: ["
						+ nextLine[1] + "]\nEmail: [" + nextLine[2] + "]");
				listCard.add(card);
			}
			return listCard;
		} catch (Exception e) {
			ScannerDBException ex = new ScannerDBException(e);
			throw ex;
		}
	}
	public void saveLocalCard(String pathToCSV){
		/// todo
	}
	public static void main() {

	}

	public static void testLocalCSV() {

	}
}
