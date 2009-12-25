package com.hideoaki.scanner.db.model;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
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
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;// 0
	private String firstName; // 1
	private String lastName; // 2
	private String position; // 3
	private String email; // 4
	private String company;// 5
	private String website;// 6
	private String address; // 7
	private String city;// 8
	private String state;// 9
	private String country;// 10
	private String zip;// 11
	private String telephone;// 12
	private String fax;// 13
	private String mobile;// 14
	private String note;// 15
	private String imgFront;// 16
	private String imgBack;// 17
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "GROUP_ID")
	private Group group;// 18
	private int privacy;// 19
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "USER_ID")
	private User user;

	@Override
	public boolean equals(Object obj) {
		// System.out.print("equal" + this.id + ":" + ((Card) obj).id);
		// System.out.print("minus " + (((Card) obj).id - this.id) );
		if (obj != null) {
			boolean a = false;
			if ((this.id - ((Card) obj).id) == 0) {
				a = true;
			}
			// System.out.println(a);
			return a;
		} else {
			return false;
		}

	}

	public Card() {
		id = -1L;
	}

	public Card(String firstName, String lastName, String position,
			String email, String company, String website, String address,
			String city, String state, String country, String zip,
			String telephone, String fax, String mobile, String note,
			String imgFront, String imgBack, Group group, int privacy) {
		id = -1L;
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

	public void setOwner(User user) {
		this.user = user;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "FNAME " + firstName + " LNAME " + lastName + " EMAIL " + email
				+ " IMG_F " + imgFront + " IMG_B " + imgBack;
	}

	public void copy(Card card) {
		this.id = card.id;
		this.firstName = card.firstName;
		this.lastName = card.lastName;
		this.position = card.position;
		this.email = card.email;
		this.company = card.company;
		this.website = card.website;
		this.address = card.address;
		this.city = card.city;
		this.state = card.state;
		this.country = card.country;
		this.zip = card.zip;
		this.telephone = card.telephone;
		this.fax = card.fax;
		this.mobile = card.mobile;
		this.note = card.note;
		this.imgFront = card.imgFront;
		this.imgBack = card.imgBack;
		if (this.group != null && card.group != null) {
			this.group.copy(card.group);
		}
		this.privacy = card.privacy;
	}

	public String[] toArray() {
		String[] arr = new String[20];
		arr[0] = String.valueOf(id);
		arr[1] = firstName;
		arr[2] = lastName;
		arr[3] = position;
		arr[4] = email;
		arr[5] = company;
		arr[6] = website;
		arr[7] = address;
		arr[8] = city;
		arr[9] = state;
		arr[10] = country;
		arr[11] = zip;
		arr[12] = telephone;
		arr[13] = fax;
		arr[14] = mobile;
		arr[15] = note;
		arr[16] = imgFront;
		arr[17] = imgBack;
		arr[18] = group == null ? "" : group.getName();
		arr[19] = String.valueOf(privacy);
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
				card.id = Long.valueOf(nextLine[0]);
				card.firstName = nextLine[1];
				card.lastName = nextLine[2];
				card.position = nextLine[3];
				card.email = nextLine[4];
				card.company = nextLine[5];
				card.website = nextLine[6];
				card.address = nextLine[7];
				card.city = nextLine[8];
				card.state = nextLine[9];
				card.country = nextLine[10];
				card.zip = nextLine[11];
				card.telephone = nextLine[12];
				card.fax = nextLine[13];
				card.mobile = nextLine[14];
				card.note = nextLine[15];
				card.imgFront = nextLine[16];
				card.imgBack = nextLine[17];
				card.group = new Group(nextLine[18]);
				card.privacy = Integer.parseInt(nextLine[19]);
				// System.out.println("Name: [" + nextLine[0] + "]\nAddress: ["
				// + nextLine[1] + "]\nEmail: [" + nextLine[2] + "]");
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
			long i = 0;
			for (Card card : cards) {
				card.id = i;
				list.add(card.toArray());
				i = i + 1;
			}
			writer.writeAll(list);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			ScannerDBException ex = new ScannerDBException(e);
			throw ex;
		}
	}

	public static List<Card> addLocalCard(Card card, String pathToCSV)
			throws ScannerDBException {
		ArrayList<Card> currentCards = loadLocalCard(pathToCSV);
		currentCards.add(card);
		saveLocalCard(currentCards, pathToCSV);
		return currentCards;
	}

	public static List<Card> deleteLocalCard(long id, String pathToCSV)
			throws ScannerDBException {
		Card temp = new Card();
		temp.setId(id);
		ArrayList<Card> currentCards = loadLocalCard(pathToCSV);
		int i = currentCards.indexOf(temp);
		if (i >= 0) {
			currentCards.remove(i);
		}
		saveLocalCard(currentCards, pathToCSV);
		return currentCards;
	}

	public static List<Card> editLocalCard(Card card, String pathToCSV)
			throws ScannerDBException {
		ArrayList<Card> currentCards = loadLocalCard(pathToCSV);
		int i = currentCards.indexOf(card);
		if (i >= 0) {
			currentCards.set(i, card);
		}
		saveLocalCard(currentCards, pathToCSV);
		return currentCards;
	}

	public static void main(String arg[]) {
		testDeleteLocalCard();
	}

	public static void testAddLocalCard() {

		Card card1 = new Card("krissada5", "chalermsook", "Project LEader",
				"hideoaki@gmail.com", "Crie Company Limited",
				"http://www.hideoaki.com", "\"400/107 \' Soi", "Bangkok",
				"ไทย", "d", "a", "025894821", "ssss", "0805511559", "aa",
				"sss", "sss", new Group("Test"), Privacy.GROUP);
		Card card2 = new Card("krissada6", "chalermsook2", "Project LEader2",
				"hideoaki@gmail.com2", "Crie Company Limited2",
				"http://www.hideoaki.com", "\"400/107 \' Soi", "Bangkok",
				"ไทย", "d", "a", "025894821", "ssss", "0805511559", "aa",
				"sss", "sss", new Group("Test"), Privacy.GROUP);
		try {
			addLocalCard(card1, DEFAULT_LOCAL_CARD_FILE);
			addLocalCard(card2, DEFAULT_LOCAL_CARD_FILE);
		} catch (ScannerDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void testDeleteLocalCard() {
		try {
			List<Card> c = deleteLocalCard(1, DEFAULT_LOCAL_CARD_FILE);
			System.out.println("after " + c.size());
		} catch (ScannerDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<Card> searchLocalCard(List<Card> cards,
			String searchKey, List<Integer> filters) throws ScannerDBException {
		List<Card> result = new ArrayList<Card>();
		// no filter condition
		if (filters == null || (filters != null && filters.size() == 0)) {
			for (Iterator iterator = cards.iterator(); iterator.hasNext();) {
				Card card = (Card) iterator.next();
				// private String firstName; // 1
				// private String lastName; // 2
				// private String position; // 3
				// private String email; // 4
				// private String company;// 5
				// private String website;// 6
				// private String address; // 7
				// private String city;// 8
				// private String state;// 9
				// private String country;// 10
				// private String zip;// 11
				// private String telephone;// 12
				// private String fax;// 13
				// private String mobile;// 14
				// private String note;// 15
				if (card.firstName.indexOf(searchKey) >= 0
						|| card.lastName.indexOf(searchKey) >= 0
						|| card.position.indexOf(searchKey) >= 0
						|| card.email.indexOf(searchKey) >= 0
						|| card.company.indexOf(searchKey) >= 0
						|| card.website.indexOf(searchKey) >= 0
						|| card.address.indexOf(searchKey) >= 0
						|| card.city.indexOf(searchKey) >= 0
						|| card.state.indexOf(searchKey) >= 0
						|| card.country.indexOf(searchKey) >= 0
						|| card.zip.indexOf(searchKey) >= 0
						|| card.telephone.indexOf(searchKey) >= 0
						|| card.fax.indexOf(searchKey) >= 0
						|| card.mobile.indexOf(searchKey) >= 0
						|| card.note.indexOf(searchKey) >= 0) {
					result.add(card);
				}
			}
		}
		// have filter
		else {
			char SEPERATOR = ' ';
			for (Iterator iterator = cards.iterator(); iterator.hasNext();) {
				StringBuffer allString = new StringBuffer();
				Card card = (Card) iterator.next();
				for (Iterator iterator2 = filters.iterator(); iterator
						.hasNext();) {
					int mode = ((Integer) iterator2.next()).intValue();
					switch (mode) {
					case 1:
						allString.append(card.firstName);
						break;
					case 2:
						allString.append(card.lastName);
						break;
					case 3:
						allString.append(card.position);
						break;
					case 4:
						allString.append(card.email);
						break;
					case 5:
						allString.append(card.company);
						break;
					case 6:
						allString.append(card.website);
						break;
					case 7:
						allString.append(card.address);
						break;
					case 8:
						allString.append(card.city);
						break;
					case 9:
						allString.append(card.state);
						break;
					case 10:
						allString.append(card.country);
						break;
					case 11:
						allString.append(card.zip);
						break;
					case 12:
						allString.append(card.telephone);
						break;
					case 13:
						allString.append(card.fax);
						break;
					case 14:
						allString.append(card.mobile);
						break;
					case 15:
						allString.append(card.note);
						break;
					default:
						break;
					}
				}
				if(allString.toString().indexOf(searchKey) >=0){
					result.add(card);
				}
			}
		}

		return result;
	}
}
