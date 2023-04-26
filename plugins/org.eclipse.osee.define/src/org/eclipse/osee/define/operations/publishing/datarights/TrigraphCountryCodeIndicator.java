/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.operations.publishing.datarights;

import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Trigaph Country Codes for Use with REL_TO and DISPLAY_ONLY Limited Dissemination Control for CUI. <a href=
 * "https://www.archives.gov/files/cui/registry/policy-guidance/registry-documents/20161214-country-trigraph-codes.pdf">CUI
 * Registry Country Codes</a>
 *
 * @author Loren K. Ashley
 */

public enum TrigraphCountryCodeIndicator implements ToMessage {

   /**
    * ARUBA
    */

   ABW("ARUBA"),

   /**
    * AFGHANISTAN
    */

   AFG("AFGHANISTAN "),

   /**
    * ANGOLA
    */

   AGO("ANGOLA"),

   /**
    * ANGUILLA
    */

   AIA("ANGUILLA"),

   /**
    * ALBANIA
    */

   ALB("ALBANIA"),

   /**
    * ANDORRA
    */

   AND("ANDORRA"),

   /**
    * UNITED ARAB EMIRATES
    */

   ARE("UNITED ARAB EMIRATES"),

   /**
    * ARGENTINA
    */

   ARG("ARGENTINA"),

   /**
    * ARMENIA
    */

   ARM("ARMENIA"),

   /**
    * AMERICAN SAMOA
    */

   ASM("AMERICAN SAMOA"),

   /**
    * ANTARCTICA
    */

   ATA("ANTARCTICA"),

   /**
    * FRENCH SOUTHERN AND ANTARCTIC LANDS
    */

   ATF("FRENCH SOUTHERN AND ANTARCTIC LANDS"),

   /**
    * ANTIGUA AND BARBUDA
    */

   ATG("ANTIGUA AND BARBUDA"),

   /**
    * AUSTRALIA
    */

   AUS("AUSTRALIA"),

   /**
    * AUSTRIA
    */

   AUT("AUSTRIA"),

   /**
    * UNKNOWN
    */

   AX1("UNKNOWN"),

   /**
    * GUANTANAMO BAY NAVAL BASE
    */

   AX2("GUANTANAMO BAY NAVAL BASE"),

   /**
    * ENTITY 6
    */

   AX3("ENTITY 6"),

   /**
    * AZERBAIJAN
    */

   AZE("AZERBAIJAN"),

   /**
    * BURUNDI
    */

   BDI("BURUNDI"),

   /**
    * BELGIUM
    */

   BEL("BELGIUM"),

   /**
    * BENIN
    */

   BEN("BENIN"),

   /**
    * BONAIRE, SINT EUSTATIUS AND SABA
    */

   BES("BONAIRE, SINT EUSTATIUS AND SABA"),

   /**
    * BURKINA FASO
    */

   BFA("BURKINA FASO"),

   /**
    * BANGLADESH
    */

   BGD("BANGLADESH"),

   /**
    * BULGARIA
    */

   BGR("BULGARIA"),

   /**
    * BAHRAIN
    */

   BHR("BAHRAIN"),

   /**
    * BAHAMAS, THE
    */

   BHS("BAHAMAS, THE"),

   /**
    * BOSNIA AND HERZEGOVINA
    */

   BIH("BOSNIA AND HERZEGOVINA"),

   /**
    * SAINT BARTHELEMY
    */

   BLM("SAINT BARTHELEMY"),

   /**
    * BELARUS
    */

   BLR("BELARUS"),

   /**
    * BELIZE
    */

   BLZ("BELIZE"),

   /**
    * BERMUDA
    */

   BMU("BERMUDA"),

   /**
    * BOLIVIA
    */

   BOL("BOLIVIA"),

   /**
    * BRAZIL
    */

   BRA("BRAZIL"),

   /**
    * BARBADOS
    */

   BRB("BARBADOS"),

   /**
    * BRUNEI
    */

   BRN("BRUNEI"),

   /**
    * BHUTAN
    */

   BTN("BHUTAN"),

   /**
    * BOUVET ISLAND
    */

   BVT("BOUVET ISLAND"),

   /**
    * BOTSWANA
    */

   BWA("BOTSWANA"),

   /**
    * CENTRAL AFRICAN REPUBLIC
    */

   CAF("CENTRAL AFRICAN REPUBLIC"),

   /**
    * CANADA
    */

   CAN("CANADA"),

   /**
    * COCOS (KEELING) ISLANDS
    */

   CCK("COCOS (KEELING) ISLANDS"),

   /**
    * SWITZERLAND
    */

   CHE("SWITZERLAND"),

   /**
    * CHILE
    */

   CHL("CHILE"),

   /**
    * CHINA
    */

   CHN("CHINA"),

   /**
    * CÔTE D'IVOIRE
    */

   CIV("CÔTE D'IVOIRE"),

   /**
    * CAMEROON
    */

   CMR("CAMEROON"),

   /**
    * CONGO(KINSHASA)
    */

   COD("CONGO(KINSHASA)"),

   /**
    * CONGO (BRAZZAVILLE)
    */

   COG("CONGO (BRAZZAVILLE)"),

   /**
    * COOK ISLANDS
    */

   COK("COOK ISLANDS"),

   /**
    * COLOMBIA
    */

   COL("COLOMBIA"),

   /**
    * COMOROS
    */

   COM("COMOROS"),

   /**
    * CLIPPERTON ISLAND
    */

   CPT("CLIPPERTON ISLAND"),

   /**
    * CABO VERDE
    */

   CPV("CABO VERDE"),

   /**
    * COSTA RICA
    */

   CRI("COSTA RICA"),

   /**
    * CUBA
    */

   CUB("CUBA"),

   /**
    * CURAÇAO
    */

   CUW("CURAÇAO"),

   /**
    * CHRISTMAS ISLAND
    */

   CXR("CHRISTMAS ISLAND"),

   /**
    * CAYMAN ISLANDS
    */

   CYM("CAYMAN ISLANDS"),

   /**
    * CYPRUS
    */

   CYP("CYPRUS"),

   /**
    * CZECH REPUBLIC
    */

   CZE("CZECH REPUBLIC"),

   /**
    * GERMANY
    */

   DEU("GERMANY"),

   /**
    * DIEGO GARCIA
    */

   DGA("DIEGO GARCIA"),

   /**
    * DJIBOUTI
    */

   DJI("DJIBOUTI"),

   /**
    * DOMINICA
    */

   DMA("DOMINICA"),

   /**
    * DENMARK
    */

   DNK("DENMARK"),

   /**
    * DOMINICAN REPUBLIC
    */

   DOM("DOMINICAN REPUBLIC"),

   /**
    * ALGERIA
    */

   DZA("ALGERIA"),

   /**
    * ECUADOR
    */

   ECU("ECUADOR"),

   /**
    * EGYPT
    */

   EGY("EGYPT"),

   /**
    * ERITREA
    */

   ERI("ERITREA"),

   /**
    * WESTERN SAHARA
    */

   ESH("WESTERN SAHARA"),

   /**
    * SPAIN
    */

   ESP("SPAIN"),

   /**
    * ESTONIA
    */

   EST("ESTONIA"),

   /**
    * ETHIOPIA
    */

   ETH("ETHIOPIA"),

   /**
    * FINLAND
    */

   FIN("FINLAND"),

   /**
    * FIJI
    */

   FJI("FIJI"),

   /**
    * FALKLAND ISLANDS (ISLAS MALVINAS)
    */

   FLK("FALKLAND ISLANDS (ISLAS MALVINAS)"),

   /**
    * FRANCE
    */

   FRA("FRANCE"),

   /**
    * FAROE ISLANDS
    */

   FRO("FAROE ISLANDS"),

   /**
    * MICRONESIA, FEDERATED STATES OF
    */

   FSM("MICRONESIA, FEDERATED STATES OF"),

   /**
    * GABON
    */

   GAB("GABON"),

   /**
    * UNITED KINGDOM
    */

   GBR("UNITED KINGDOM"),

   /**
    * GEORGIA
    */

   GEO("GEORGIA"),

   /**
    * GUERNSEY
    */

   GGY("GUERNSEY"),

   /**
    * GHANA
    */

   GHA("GHANA"),

   /**
    * GIBRALTAR
    */

   GIB("GIBRALTAR"),

   /**
    * GUINEA
    */

   GIN("GUINEA"),

   /**
    * GUADELOUPE
    */

   GLP("GUADELOUPE"),

   /**
    * GAMBIA, THE
    */

   GMB("GAMBIA, THE"),

   /**
    * GUINEA-BISSAU
    */

   GNB("GUINEA-BISSAU"),

   /**
    * EQUATORIAL GUINEA
    */

   GNQ("EQUATORIAL GUINEA"),

   /**
    * GREECE
    */

   GRC("GREECE"),

   /**
    * GRENADA
    */

   GRD("GRENADA"),

   /**
    * GREENLAND
    */

   GRL("GREENLAND"),

   /**
    * GUATEMALA
    */

   GTM("GUATEMALA"),

   /**
    * FRENCH GUIANA
    */

   GUF("FRENCH GUIANA"),

   /**
    * GUAM
    */

   GUM("GUAM"),

   /**
    * GUYANA
    */

   GUY("GUYANA"),

   /**
    * HONG KONG
    */

   HKG("HONG KONG"),

   /**
    * HEARD ISLAND AND MCDONALD ISLANDS
    */

   HMD("HEARD ISLAND AND MCDONALD ISLANDS"),

   /**
    * HONDURAS
    */

   HND("HONDURAS"),

   /**
    * CROATIA
    */

   HRV("CROATIA"),

   /**
    * HAITI
    */

   HTI("HAITI"),

   /**
    * HUNGARY
    */

   HUN("HUNGARY"),

   /**
    * INDONESIA
    */

   IDN("INDONESIA"),

   /**
    * ISLE OF MAN
    */

   IMN("ISLE OF MAN"),

   /**
    * INDIA
    */

   IND("INDIA"),

   /**
    * BRITISH INDIAN OCEAN TERRITORY
    */

   IOT("BRITISH INDIAN OCEAN TERRITORY"),

   /**
    * IRELAND
    */

   IRL("IRELAND"),

   /**
    * IRAN
    */

   IRN("IRAN"),

   /**
    * IRAQ
    */

   IRQ("IRAQ"),

   /**
    * ICELAND
    */

   ISL("ICELAND"),

   /**
    * ISRAEL
    */

   ISR("ISRAEL"),

   /**
    * ITALY
    */

   ITA("ITALY"),

   /**
    * JAMAICA
    */

   JAM("JAMAICA"),

   /**
    * JERSEY
    */

   JEY("JERSEY"),

   /**
    * JORDAN
    */

   JOR("JORDAN"),

   /**
    * JAPAN
    */

   JPN("JAPAN"),

   /**
    * KAZAKHSTAN
    */

   KAZ("KAZAKHSTAN"),

   /**
    * KENYA
    */

   KEN("KENYA"),

   /**
    * KYRGYZSTAN
    */

   KGZ("KYRGYZSTAN"),

   /**
    * CAMBODIA
    */

   KHM("CAMBODIA"),

   /**
    * KIRIBATI
    */

   KIR("KIRIBATI"),

   /**
    * SAINT KITTS AND NEVIS
    */

   KNA("SAINT KITTS AND NEVIS"),

   /**
    * KOREA, SOUTH
    */

   KOR("KOREA, SOUTH"),

   /**
    * KUWAIT
    */

   KWT("KUWAIT"),

   /**
    * LAOS
    */

   LAO("LAOS"),

   /**
    * LEBANON
    */

   LBN("LEBANON"),

   /**
    * LIBERIA
    */

   LBR("LIBERIA"),

   /**
    * LIBYA
    */

   LBY("LIBYA"),

   /**
    * SAINT LUCIA
    */

   LCA("SAINT LUCIA"),

   /**
    * LIECHTENSTEIN
    */

   LIE("LIECHTENSTEIN"),

   /**
    * SRI LANKA
    */

   LKA("SRI LANKA"),

   /**
    * LESOTHO
    */

   LSO("LESOTHO"),

   /**
    * LITHUANIA
    */

   LTU("LITHUANIA"),

   /**
    * LUXEMBOURG
    */

   LUX("LUXEMBOURG"),

   /**
    * LATVIA
    */

   LVA("LATVIA"),

   /**
    * MACAU
    */

   MAC("MACAU"),

   /**
    * SAINT MARTIN
    */

   MAF("SAINT MARTIN"),

   /**
    * MOROCCO
    */

   MAR("MOROCCO"),

   /**
    * MONACO
    */

   MCO("MONACO"),

   /**
    * MOLDOVA
    */

   MDA("MOLDOVA"),

   /**
    * MADAGASCAR
    */

   MDG("MADAGASCAR"),

   /**
    * MALDIVES
    */

   MDV("MALDIVES"),

   /**
    * MEXICO
    */

   MEX("MEXICO"),

   /**
    * MARSHALL ISLANDS
    */

   MHL("MARSHALL ISLANDS"),

   /**
    * MACEDONIA
    */

   MKD("MACEDONIA"),

   /**
    * MALI
    */

   MLI("MALI"),

   /**
    * MALTA
    */

   MLT("MALTA"),

   /**
    * BURMA
    */

   MMR("BURMA"),

   /**
    * MONTENEGRO
    */

   MNE("MONTENEGRO"),

   /**
    * MONGOLIA
    */

   MNG("MONGOLIA"),

   /**
    * NORTHERN MARIANA ISLANDS
    */

   MNP("NORTHERN MARIANA ISLANDS"),

   /**
    * MOZAMBIQUE
    */

   MOZ("MOZAMBIQUE"),

   /**
    * MAURITANIA
    */

   MRT("MAURITANIA"),

   /**
    * MONTSERRAT
    */

   MSR("MONTSERRAT"),

   /**
    * MARTINIQUE
    */

   MTQ("MARTINIQUE"),

   /**
    * MAURITIUS
    */

   MUS("MAURITIUS"),

   /**
    * MALAWI
    */

   MWI("MALAWI"),

   /**
    * MALAYSIA
    */

   MYS("MALAYSIA"),

   /**
    * MAYOTTE
    */

   MYT("MAYOTTE"),

   /**
    * NAMIBIA
    */

   NAM("NAMIBIA"),

   /**
    * NEW CALEDONIA
    */

   NCL("NEW CALEDONIA"),

   /**
    * NIGER
    */

   NER("NIGER"),

   /**
    * NORFOLK ISLAND
    */

   NFK("NORFOLK ISLAND"),

   /**
    * NIGERIA
    */

   NGA("NIGERIA"),

   /**
    * NICARAGUA
    */

   NIC("NICARAGUA"),

   /**
    * NIUE
    */

   NIU("NIUE"),

   /**
    * NETHERLANDS
    */

   NLD("NETHERLANDS"),

   /**
    * NORWAY
    */

   NOR("NORWAY"),

   /**
    * NEPAL
    */

   NPL("NEPAL"),

   /**
    * NAURU
    */

   NRU("NAURU"),

   /**
    * NEW ZEALAND
    */

   NZL("NEW ZEALAND"),

   /**
    * OMAN
    */

   OMN("OMAN"),

   /**
    * PAKISTAN
    */

   PAK("PAKISTAN"),

   /**
    * PANAMA
    */

   PAN("PANAMA"),

   /**
    * PITCAIRN ISLANDS
    */

   PCN("PITCAIRN ISLANDS"),

   /**
    * PERU
    */

   PER("PERU"),

   /**
    * PHILIPPINES
    */

   PHL("PHILIPPINES"),

   /**
    * PALAU
    */

   PLW("PALAU"),

   /**
    * PAPUA NEW GUINEA
    */

   PNG("PAPUA NEW GUINEA"),

   /**
    * POLAND
    */

   POL("POLAND"),

   /**
    * PUERTO RICO
    */

   PRI("PUERTO RICO"),

   /**
    * KOREA, NORTH
    */

   PRK("KOREA, NORTH"),

   /**
    * PORTUGAL
    */

   PRT("PORTUGAL"),

   /**
    * PARAGUAY
    */

   PRY("PARAGUAY"),

   /**
    * FRENCH POLYNESIA
    */

   PYF("FRENCH POLYNESIA"),

   /**
    * QATAR
    */

   QAT("QATAR"),

   /**
    * REUNION
    */

   REU("REUNION"),

   /**
    * ROMANIA
    */

   ROU("ROMANIA"),

   /**
    * RUSSIA
    */

   RUS("RUSSIA"),

   /**
    * RWANDA
    */

   RWA("RWANDA"),

   /**
    * SAUDI ARABIA
    */

   SAU("SAUDI ARABIA"),

   /**
    * SUDAN
    */

   SDN("SUDAN"),

   /**
    * SENEGAL
    */

   SEN("SENEGAL"),

   /**
    * SINGAPORE
    */

   SGP("SINGAPORE"),

   /**
    * SOUTH GEORGIA AND SOUTH SANDWICH ISLANDS
    */

   SGS("SOUTH GEORGIA AND SOUTH SANDWICH ISLANDS"),

   /**
    * SAINT HELENA, ASCENSION AND TRISTAN DA CUNHA
    */

   SHN("SAINT HELENA, ASCENSION AND TRISTAN DA CUNHA"),

   /**
    * SOLOMON ISLANDS
    */

   SLB("SOLOMON ISLANDS"),

   /**
    * SIERRA LEONE
    */

   SLE("SIERRA LEONE"),

   /**
    * EL SALVADOR
    */

   SLV("EL SALVADOR"),

   /**
    * SAN MARINO
    */

   SMR("SAN MARINO"),

   /**
    * SOMALIA
    */

   SOM("SOMALIA"),

   /**
    * SAINT PIERRE AND MIQUELON
    */

   SPM("SAINT PIERRE AND MIQUELON"),

   /**
    * SERBIA
    */

   SRB("SERBIA"),

   /**
    * SOUTH SUDAN
    */

   SSD("SOUTH SUDAN"),

   /**
    * SAO TOME AND PRINCIPE
    */

   STP("SAO TOME AND PRINCIPE"),

   /**
    * SURINAME
    */

   SUR("SURINAME"),

   /**
    * SLOVAKIA
    */

   SVK("SLOVAKIA"),

   /**
    * SLOVENIA
    */

   SVN("SLOVENIA"),

   /**
    * SWEDEN
    */

   SWE("SWEDEN"),

   /**
    * SWAZILAND
    */

   SWZ("SWAZILAND"),

   /**
    * SINT MAARTEN
    */

   SXM("SINT MAARTEN"),

   /**
    * SEYCHELLES
    */

   SYC("SEYCHELLES"),

   /**
    * SYRIA
    */

   SYR("SYRIA"),

   /**
    * TURKS AND CAICOS ISLANDS
    */

   TCA("TURKS AND CAICOS ISLANDS"),

   /**
    * CHAD
    */

   TCD("CHAD"),

   /**
    * TOGO
    */

   TGO("TOGO"),

   /**
    * THAILAND
    */

   THA("THAILAND"),

   /**
    * TAJIKISTAN
    */

   TJK("TAJIKISTAN"),

   /**
    * TOKELAU
    */

   TKL("TOKELAU"),

   /**
    * TURKMENISTAN
    */

   TKM("TURKMENISTAN"),

   /**
    * TIMOR-LESTE
    */

   TLS("TIMOR-LESTE"),

   /**
    * TONGA
    */

   TON("TONGA"),

   /**
    * TRINIDAD AND TOBAGO
    */

   TTO("TRINIDAD AND TOBAGO"),

   /**
    * TUNISIA
    */

   TUN("TUNISIA"),

   /**
    * TURKEY
    */

   TUR("TURKEY"),

   /**
    * TUVALU
    */

   TUV("TUVALU"),

   /**
    * TAIWAN
    */

   TWN("TAIWAN"),

   /**
    * TANZANIA
    */

   TZA("TANZANIA"),

   /**
    * UGANDA
    */

   UGA("UGANDA"),

   /**
    * UKRAINE
    */

   UKR("UKRAINE"),

   /**
    * URUGUAY
    */

   URY("URUGUAY"),

   /**
    * UNITED STATES
    */

   USA("UNITED STATES"),

   /**
    * UZBEKISTAN
    */

   UZB("UZBEKISTAN"),

   /**
    * HOLY SEE (VATICAN CITY STATE)
    */

   VAT("HOLY SEE (VATICAN CITY STATE)"),

   /**
    * SAINT VINCENT AND THE GRENADINES
    */

   VCT("SAINT VINCENT AND THE GRENADINES"),

   /**
    * VENEZUELA
    */

   VEN("VENEZUELA"),

   /**
    * VIRGIN ISLANDS, BRITISH
    */

   VGB("VIRGIN ISLANDS, BRITISH"),

   /**
    * VIRGIN ISLANDS, U.S.
    */

   VIR("VIRGIN ISLANDS, U.S."),

   /**
    * VIETNAM
    */

   VNM("VIETNAM"),

   /**
    * VANUATU
    */

   VUT("VANUATU"),

   /**
    * WALLIS AND FUTUNA
    */

   WLF("WALLIS AND FUTUNA"),

   /**
    * SAMOA
    */

   WSM("SAMOA"),

   /**
    * ASHMORE AND CARTIER ISLANDS
    */

   XAC("ASHMORE AND CARTIER ISLANDS"),

   /**
    * ENTITY 1
    */

   XAZ("ENTITY 1"),

   /**
    * BASSAS DA INDIA
    */

   XBI("BASSAS DA INDIA"),

   /**
    * BAKER ISLAND
    */

   XBK("BAKER ISLAND"),

   /**
    * ENTITY 2
    */

   XCR("ENTITY 2"),

   /**
    * CORAL SEA ISLANDS
    */

   XCS("CORAL SEA ISLANDS"),

   /**
    * ENTITY 3
    */

   XCY("ENTITY 3"),

   /**
    * EUROPA ISLAND
    */

   XEU("EUROPA ISLAND"),

   /**
    * GLORIOSO ISLANDS
    */

   XGL("GLORIOSO ISLANDS"),

   /**
    * GAZA STRIP
    */

   XGZ("GAZA STRIP"),

   /**
    * HOWLAND ISLAND
    */

   XHO("HOWLAND ISLAND"),

   /**
    * JOHNSTON ATOLL
    */

   XJA("JOHNSTON ATOLL"),

   /**
    * JAN MAYEN
    */

   XJM("JAN MAYEN"),

   /**
    * JUAN DE NOVA ISLAND
    */

   XJN("JUAN DE NOVA ISLAND"),

   /**
    * JARVIS ISLAND
    */

   XJV("JARVIS ISLAND"),

   /**
    * ENTITY 4
    */

   XKM("ENTITY 4"),

   /**
    * ENTITY 5
    */

   XKN("ENTITY 5"),

   /**
    * KINGMAN REEF
    */

   XKR("KINGMAN REEF"),

   /**
    * KOSOVO
    */

   XKS("KOSOVO"),

   /**
    * MIDWAY ISLANDS
    */

   XMW("MIDWAY ISLANDS"),

   /**
    * NAVASSA ISLAND
    */

   XNV("NAVASSA ISLAND"),

   /**
    * PALMYRA ATOLL
    */

   XPL("PALMYRA ATOLL"),

   /**
    * PARACEL ISLANDS
    */

   XPR("PARACEL ISLANDS"),

   /**
    * AKROTIRI
    */

   XQZ("AKROTIRI "),

   /**
    * SPRATLY ISLANDS
    */

   XSP("SPRATLY ISLANDS"),

   /**
    * SVALBARD
    */

   XSV("SVALBARD"),

   /**
    * TROMELIN ISLAND
    */

   XTR("TROMELIN ISLAND"),

   /**
    * WEST BANK
    */

   XWB("WEST BANK"),

   /**
    * WAKE ISLAND
    */

   XWK("WAKE ISLAND"),

   /**
    * DHEKELIA
    */

   XXD("DHEKELIA"),

   /**
    * YEMEN
    */

   YEM("YEMEN"),

   /**
    * SOUTH AFRICA
    */

   ZAF("SOUTH AFRICA"),

   /**
    * ZAMBIA
    */

   ZMB("ZAMBIA"),

   /**
    * ZIMBABWE
    */

   ZWE("ZIMBABWE");

   private String countryName;

   private TrigraphCountryCodeIndicator(String countryName) {
      this.countryName = countryName;
   }

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = Objects.isNull(message) ? new Message() : message;
      outMessage.segment(this.name(), this.countryName);
      return outMessage;
   }

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }
}

/* EOF */
