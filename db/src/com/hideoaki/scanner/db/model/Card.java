package com.hideoaki.scanner.db.model;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.hideoaki.scanner.db.utils.Privacy;
import com.hideoaki.scanner.db.utils.ScannerDBException;
@Entity
@Table(name = "Card")
public class Card {
	public static final String DEFAULT_LOCAL_CARD_FILE = "defaultcard.csv";
	@Id @GeneratedValue
    @Column(name = "ID")
    private Long id;
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
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "GROUP_ID")
	private Group group;
	private int privacy;

	public Card() {

	}

	public Card(String firstName, String lastName, String position,
			String email, String company, String website, String address,
			String city, String state, String country, String zip,
			String telephone, String fax, String mobile, String note,
			String imgFront, String imgBack, Group group, int privacy) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.position = position;
		this.email = email;
		this.company = company;
		this.website = website;
		this.address = address;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zip = zip;
		this.telephone = telephone;
		this.fax = fax;
		this.mobile = mobile;
		this.note = note;
		this.imgFront = imgFront;
		this.imgBack = imgBack;
		this.group = group;
		this.privacy = privacy;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
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
	
	@Override
	public String toString() {
		return "FNAME " + firstName + " LNAME "+ lastName + " EMAIL " + email + " IMG_F "+ imgFront+ " IMG_B " + imgBack;
	}

	public String[] toArray() {
		String[] arr = new String[19];
		arr[0] = firstName;
		arr[1] = lastName;
		arr[2] = position;
		arr[3] = email;
		arr[4] = company;
		arr[5] = website;
		arr[6] = address;
		arr[7] = city;
		arr[8] = state;
		arr[9] = country;
		arr[10] = zip;
		arr[11] = telephone;
		arr[12] = fax;
		arr[13] = mobile;
		arr[14] = note;
		arr[15] = imgFront;
		arr[16] = imgBack;
		arr[17] = group == null ? "" : group.getName();
		arr[18] = String.valueOf(privacy);
		return arr;
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
				card.firstName = nextLine[0];
				card.lastName = nextLine[1];
				card.position = nextLine[2];
				card.email = nextLine[3];
				card.company = nextLine[4];
				card.website = nextLine[5];
				card.address = nextLine[6];
				card.city = nextLine[7];
				card.state = nextLine[8];
				card.country = nextLine[9];
				card.zip = nextLine[10];
				card.telephone = nextLine[11];
				card.fax = nextLine[12];
				card.mobile = nextLine[13];
				card.note = nextLine[14];
				card.imgFront = nextLine[15];
				card.imgBack = nextLine[16];
				card.group = new Group(nextLine[17]);
				card.privacy = Integer.parseInt(nextLine[18]);
				System.out.println("Name: [" + nextLine[0] + "]\nAddress: ["
						+ nextLine[1] + "]\nEmail: [" + nextLine[2] + "]");
				listCard.add(card);
			}
			reader.close();
			return listCard;
		} catch (Exception e) {
			ScannerDBException ex = new ScannerDBException(e);
			throw ex;
		}
	}

	public static void saveLocalCard(List<Card> cards, String pathToCSV)
			throws ScannerDBException {
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(pathToCSV));
			List<String[]> list = new ArrayList<String[]>();
			for (Card card : cards) {
				list.add(card.toArray());
			}
			writer.writeAll(list);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			ScannerDBException ex = new ScannerDBException(e);
			throw ex;
		}
	}

	public static void main(String arg[]) {
		testLoadLocalCSV();
	}

	public static void testSaveLocalCSV() {

		List<Card> cards = new ArrayList<Card>();
		Card card1 = new Card("krissada", "chalermsook", "Project LEader",
				"hideoaki@gmail.com", "Crie Company Limited",
				"http://www.hideoaki.com", "\"400/107 \' Soi", "Bangkok",
				"ไทย", "d", "a", "025894821", "ssss", "0805511559", "aa",
				"sss", "sss", new Group("Test"), Privacy.GROUP);
		Card card2 = new Card("krissada2", "chalermsook2", "Project LEader2",
				"hideoaki@gmail.com2", "Crie Company Limited2",
				"http://www.hideoaki.com", "\"400/107 \' Soi", "Bangkok",
				"ไทย", "d", "a", "025894821", "ssss", "0805511559", "aa",
				"sss", "sss", new Group("Test"), Privacy.GROUP);
		cards.add(card1);
		cards.add(card2);
		try {
			saveLocalCard(cards, DEFAULT_LOCAL_CARD_FILE);
		} catch (ScannerDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	public static void testLoadLocalCSV() {
		try {
			List<Card> list = loadLocalCard(DEFAULT_LOCAL_CARD_FILE);
			System.out.println(list.size());
		} catch (ScannerDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
