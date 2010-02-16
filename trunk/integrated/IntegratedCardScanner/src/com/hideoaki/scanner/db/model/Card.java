package com.hideoaki.scanner.db.model;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.hideoaki.scanner.db.utils.Privacy;
import com.hideoaki.scanner.db.utils.ScannerDBException;

public class Card {

    public static final String DEFAULT_LOCAL_CARD_FILE = "defaultcard.csv";
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
    private String firstNameE;
    private String lastNameE;
    private String positionE;
    private String companyE;
    private String addressE;
    private String cityE;
    private String stateE;
    private String countryE;
    private String zipE;
    private String telephoneE;
    private String faxE;
    private String mobileE;
    private String noteE;

    public String getAddressE() {
        return addressE;
    }

    public void setAddressE(String addressE) {
        this.addressE = addressE;
    }

    public String getCityE() {
        return cityE;
    }

    public void setCityE(String cityE) {
        this.cityE = cityE;
    }

    public String getCompanyE() {
        return companyE;
    }

    public void setCompanyE(String companyE) {
        this.companyE = companyE;
    }

    public String getCountryE() {
        return countryE;
    }

    public void setCountryE(String countryE) {
        this.countryE = countryE;
    }

    public String getFaxE() {
        return faxE;
    }

    public void setFaxE(String faxE) {
        this.faxE = faxE;
    }

    public String getFirstNameE() {
        return firstNameE;
    }

    public void setFirstNameE(String firstNameE) {
        this.firstNameE = firstNameE;
    }

    public String getLastNameE() {
        return lastNameE;
    }

    public void setLastNameE(String lastNameE) {
        this.lastNameE = lastNameE;
    }

    public String getMobileE() {
        return mobileE;
    }

    public void setMobileE(String mobileE) {
        this.mobileE = mobileE;
    }

    public String getNoteE() {
        return noteE;
    }

    public void setNoteE(String noteE) {
        this.noteE = noteE;
    }

    public String getPositionE() {
        return positionE;
    }

    public void setPositionE(String positionE) {
        this.positionE = positionE;
    }

    public String getStateE() {
        return stateE;
    }

    public void setStateE(String stateE) {
        this.stateE = stateE;
    }

    public String getTelephoneE() {
        return telephoneE;
    }

    public void setTelephoneE(String telephoneE) {
        this.telephoneE = telephoneE;
    }

    public String getZipE() {
        return zipE;
    }

    public void setZipE(String zipE) {
        this.zipE = zipE;
    }
    private String imgFront;
    private String imgBack;

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
            String imgFront, String imgBack,
            String firstNameE, String lastNameE, String positionE,
            String companyE, String addressE,
            String cityE, String stateE, String countryE, String zipE,
            String telephoneE, String faxE, String mobileE, String noteE) {
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

        this.firstNameE = firstNameE;
        this.lastNameE = lastNameE;
        this.positionE = positionE;
        this.companyE = companyE;
        this.addressE = addressE;
        this.cityE = cityE;
        this.stateE = stateE;
        this.countryE = countryE;
        this.zipE = zipE;
        this.telephoneE = telephoneE;
        this.faxE = faxE;
        this.mobileE = mobileE;
        this.noteE = noteE;

    }

    public void setAllValue(String[] s) {
        this.firstName = s[1];
        this.lastName = s[2];
        this.position = s[3];
        this.email = s[4];
        this.company = s[5];
        this.website = s[6];
        this.address = s[7];
        this.city = s[8];
        this.state = s[9];
        this.country = s[10];
        this.zip = s[11];
        this.telephone = s[12];
        this.fax = s[13];
        this.mobile = s[14];
        this.note = s[15];

        this.imgFront = s[16];
        this.imgBack = s[17];

        this.firstNameE = s[18];
        this.lastNameE = s[19];
        this.positionE = s[20];
        this.companyE = s[21];
        this.addressE = s[22];
        this.cityE = s[23];
        this.stateE = s[24];
        this.countryE = s[25];
        this.zipE = s[26];
        this.telephoneE = s[27];
        this.faxE = s[28];
        this.mobileE = s[29];
        this.noteE = s[30];
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

        this.firstNameE = card.firstNameE;
        this.lastNameE = card.lastNameE;
        this.positionE = card.positionE;
        this.companyE = card.companyE;
        this.addressE = card.addressE;
        this.cityE = card.cityE;
        this.stateE = card.stateE;
        this.countryE = card.countryE;
        this.zipE = card.zipE;
        this.telephoneE = card.telephoneE;
        this.faxE = card.faxE;
        this.mobileE = card.mobileE;
        this.noteE = card.noteE;
    }

    public String[] toArray() {
        String[] arr = new String[31];
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
        arr[18] = firstNameE;
        arr[19] = lastNameE;
        arr[20] = positionE;
        arr[21] = companyE;
        arr[22] = addressE;
        arr[23] = cityE;
        arr[24] = stateE;
        arr[25] = countryE;
        arr[26] = zipE;
        arr[27] = telephoneE;
        arr[28] = faxE;
        arr[29] = mobileE;
        arr[30] = noteE;

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

                card.firstNameE = nextLine[18];
                card.lastNameE = nextLine[19];
                card.positionE = nextLine[20];
                card.companyE = nextLine[21];
                card.addressE = nextLine[22];
                card.cityE = nextLine[23];
                card.stateE = nextLine[24];
                card.countryE = nextLine[25];
                card.zipE = nextLine[26];
                card.telephoneE = nextLine[27];
                card.faxE = nextLine[28];
                card.mobileE = nextLine[29];
                card.noteE = nextLine[30];

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

 

//	public static void testAddLocalCard() {
//
//		Card card1 = new Card("krissada5", "chalermsook", "Project LEader",
//				"hideoaki@gmail.com", "Crie Company Limited",
//				"http://www.hideoaki.com", "\"400/107 \' Soi", "Bangkok",
//				"��", "d", "a", "025894821", "ssss", "0805511559", "aa",
//				"sss", "sss");
//		Card card2 = new Card("krissada6", "chalermsook2", "Project LEader2",
//				"hideoaki@gmail.com2", "Crie Company Limited2",
//				"http://www.hideoaki.com", "\"400/107 \' Soi", "Bangkok",
//				"��", "d", "a", "025894821", "ssss", "0805511559", "aa",
//				"sss", "sss");
//		try {
//			addLocalCard(card1, DEFAULT_LOCAL_CARD_FILE);
//			addLocalCard(card2, DEFAULT_LOCAL_CARD_FILE);
//		} catch (ScannerDBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	public static void testDeleteLocalCard() {
//		try {
//			List<Card> c = deleteLocalCard(1, DEFAULT_LOCAL_CARD_FILE);
//			System.out.println("after " + c.size());
//		} catch (ScannerDBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
   }
