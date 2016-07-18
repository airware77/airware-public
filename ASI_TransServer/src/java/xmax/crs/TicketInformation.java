package xmax.crs;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketInformation implements Serializable
{
	private String authID;
	private int authSequence;
	private String psgrID;
	private int psgrSequence;
	private int pnrLineNumber;
	private String ticketNumber;
	private BigDecimal ticketValue;
	private boolean eticket;
	private String currencyCode;
	private Date ticketDate;
	private String agency;
	private String officeID;
	private String agent;
	private String item;
	private BigDecimal ccAmount;
	private BigDecimal cashAmount;
	private BigDecimal taxAmount;
	private BigDecimal feeAmount;
	private BigDecimal commissionAmount;
	private String documentNumber;
	private String psgrName;
	private String tourCode;
	private String invoice;
	private String fop1;
	private String fop2;
	private String fop3;
	private Map<String, BigDecimal> taxes;
	private String exchangeValue;
	private String newTicket;
	private String origin;
	private String purchaser;
	private String fareLadder;
	private boolean automated;
	private List<Integer> segments;
	
	public String getTicketNumber()
	{
		return ticketNumber;
	}
	public void setTicketNumber(String ticketNumber)
	{
		this.ticketNumber = ticketNumber;
	}
	public int getPsgrSequence()
	{
		return psgrSequence;
	}
	public void setPsgrSequence(int psgrSequence)
	{
		this.psgrSequence = psgrSequence;
	}
	public int getPnrLineNumber()
	{
		return pnrLineNumber;
	}
	public void setPnrLineNumber(int pnrLineNumber)
	{
		this.pnrLineNumber = pnrLineNumber;
	}
	public String getAuthID()
	{
		return authID;
	}
	public void setAuthID(String authID)
	{
		this.authID = authID;
	}
	public int getAuthSequence()
	{
		return authSequence;
	}
	public void setAuthSequence(int authSequence)
	{
		this.authSequence = authSequence;
	}
	public String getPsgrID()
	{
		return psgrID;
	}
	public void setPsgrID(String psgrID)
	{
		this.psgrID = psgrID;
	}
	public BigDecimal getTicketValue()
	{
		return ticketValue;
	}
	public void setTicketValue(BigDecimal ticketValue)
	{
		this.ticketValue = ticketValue;
	}
	public boolean isEticket()
	{
		return eticket;
	}
	public void setEticket(boolean eticket)
	{
		this.eticket = eticket;
	}
	public String getCurrencyCode()
	{
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode)
	{
		this.currencyCode = currencyCode;
	}
	public Date getTicketDate()
	{
		return ticketDate;
	}
	public void setTicketDate(Date ticketDate)
	{
		this.ticketDate = ticketDate;
	}
	public String getAgency()
	{
		return agency;
	}
	public void setAgency(String agency)
	{
		this.agency = agency;
	}
	public String getOfficeID()
	{
		return officeID;
	}
	public void setOfficeID(String officeID)
	{
		this.officeID = officeID;
	}
	public String getAgent()
	{
		return agent;
	}
	public void setAgent(String agent)
	{
		this.agent = agent;
	}
	public String getItem()
	{
		return item;
	}
	public void setItem(String item)
	{
		this.item = item;
	}
	public BigDecimal getCcAmount()
	{
		return ccAmount;
	}
	public void setCcAmount(BigDecimal ccAmount)
	{
		this.ccAmount = ccAmount;
	}
	public BigDecimal getCashAmount()
	{
		return cashAmount;
	}
	public void setCashAmount(BigDecimal cashAmount)
	{
		this.cashAmount = cashAmount;
	}
	public BigDecimal getTaxAmount()
	{
		return taxAmount;
	}
	public void setTaxAmount(BigDecimal taxAmount)
	{
		this.taxAmount = taxAmount;
	}
	public BigDecimal getFeeAmount()
	{
		return feeAmount;
	}
	public void setFeeAmount(BigDecimal feeAmount)
	{
		this.feeAmount = feeAmount;
	}
	public BigDecimal getCommissionAmount()
	{
		return commissionAmount;
	}
	public void setCommissionAmount(BigDecimal commissionAmount)
	{
		this.commissionAmount = commissionAmount;
	}
	public String getDocumentNumber()
	{
		return documentNumber;
	}
	public void setDocumentNumber(String documentNumber)
	{
		this.documentNumber = documentNumber;
	}
	public String getPsgrName()
	{
		return psgrName;
	}
	public void setPsgrName(String psgrName)
	{
		this.psgrName = psgrName;
	}
	public String getTourCode()
	{
		return tourCode;
	}
	public void setTourCode(String tourCode)
	{
		this.tourCode = tourCode;
	}
	public String getInvoice()
	{
		return invoice;
	}
	public void setInvoice(String invoice)
	{
		this.invoice = invoice;
	}
	public String getFop1()
	{
		return fop1;
	}
	public void setFop1(String fop1)
	{
		this.fop1 = fop1;
	}
	public String getFop2()
	{
		return fop2;
	}
	public void setFop2(String fop2)
	{
		this.fop2 = fop2;
	}
	public String getFop3()
	{
		return fop3;
	}
	public void setFop3(String fop3)
	{
		this.fop3 = fop3;
	}
	public Map<String, BigDecimal> getTaxes()
	{
		if (taxes == null)
		{
			taxes = new HashMap<String, BigDecimal>();
		}
		return taxes;
	}
	public void setTaxes(Map<String, BigDecimal> taxes)
	{
		this.taxes = taxes;
	}
	public String getExchangeValue()
	{
		return exchangeValue;
	}
	public void setExchangeValue(String exchangeValue)
	{
		this.exchangeValue = exchangeValue;
	}
	public String getNewTicket()
	{
		return newTicket;
	}
	public void setNewTicket(String newTicket)
	{
		this.newTicket = newTicket;
	}
	public String getOrigin()
	{
		return origin;
	}
	public void setOrigin(String origin)
	{
		this.origin = origin;
	}
	public String getPurchaser()
	{
		return purchaser;
	}
	public void setPurchaser(String purchaser)
	{
		this.purchaser = purchaser;
	}
	public String getFareLadder()
	{
		return fareLadder;
	}
	public void setFareLadder(String fareLadder)
	{
		this.fareLadder = fareLadder;
	}
	public boolean isAutomated()
	{
		return automated;
	}
	public void setAutomated(boolean automated)
	{
		this.automated = automated;
	}
	public List<Integer> getSegments()
	{
		if (this.segments == null)
		{
			this.segments = new ArrayList<Integer>();
		}
		return segments;
	}
	public void setSegments(List<Integer> segments)
	{
		this.segments = segments;
	}
	
	
	public String writeInfo()
	{
		final StringBuilder sBuf = new StringBuilder();
		
		sBuf.append("lineNum=" + this.pnrLineNumber + "\r\n");
		sBuf.append("authID=" + this.authID + "\r\n");
		sBuf.append("authSeq=" + this.authSequence + "\r\n");
		sBuf.append("psgrID=" + this.psgrID + "\r\n");
		sBuf.append("psgrSeq=" + this.psgrSequence + "\r\n");
		sBuf.append("ticketNum=" + this.ticketNumber + "\r\n");
		sBuf.append("ticketValue=" + this.ticketValue.toPlainString() + "\r\n");
		sBuf.append("Eticket=" + this.eticket + "\r\n");
		sBuf.append("Automated=" + this.automated + "\r\n");
		sBuf.append("Currency=" + this.currencyCode + "\r\n");
		sBuf.append("TicketDate=" + this.ticketDate.toString() + "\r\n");
		sBuf.append("OfficeID=" + this.officeID + "\r\n");
		sBuf.append("Agency=" + this.agency + "\r\n");
		sBuf.append("Agent=" + this.agent + "\r\n");
		sBuf.append("Item=" + this.item + "\r\n");
		sBuf.append("CCAmount=" + this.ccAmount + "\r\n");
		sBuf.append("CashAmount=" + this.cashAmount + "\r\n");
		sBuf.append("TaxAmount=" + this.taxAmount + "\r\n");
		sBuf.append("FeeAmount=" + this.feeAmount + "\r\n");
		sBuf.append("CommAmount=" + this.commissionAmount + "\r\n");
		sBuf.append("Document=" + this.documentNumber + "\r\n");
		sBuf.append("PsgrName=" + this.psgrName + "\r\n");
		sBuf.append("TourCode=" + this.tourCode + "\r\n");
		sBuf.append("Invoice=" + this.invoice + "\r\n");
		sBuf.append("Fop1=" + this.fop1 + "\r\n");
		sBuf.append("Fop2=" + this.fop2 + "\r\n");
		sBuf.append("Fop3=" + this.fop3 + "\r\n");
		sBuf.append("Taxes=" + this.taxes.toString() + "\r\n");
		sBuf.append("Exchange=" + this.exchangeValue + "\r\n");
		sBuf.append("NewTicket=" + this.newTicket + "\r\n");
		sBuf.append("Origin=" + this.origin + "\r\n");
		sBuf.append("Purchaser=" + this.purchaser + "\r\n");
		sBuf.append("FareLadder=" + this.fareLadder + "\r\n");
		sBuf.append("Segments=" + this.segments.toString() + "\r\n");
		
		return sBuf.toString();
	}
}
