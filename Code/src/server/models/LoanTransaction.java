package server.models;

import server.models.enums.LoanStatus;
import java.io.Serializable;
import java.util.Date;

public class LoanTransaction implements Serializable {
    // stores a unique id for the transaction
    private String transactionID;
    // stores the id of the member who borrowed the resource
    private String memberID;
    // stores the id of the borrowed resource
    private String resourceID;
    // date when the resource was issued
    private Date issueDate;
    // due date for returning the resource
    private Date dueDate;
    // actual date the resource was returned
    private Date returnDate;
    // status of the transaction: checked_out or returned
    private LoanStatus status;

    // constructor initializes a new loan with issued and due dates, status is checked_out by default
    public LoanTransaction(String transactionID, String memberID, String resourceID, Date issueDate, Date dueDate) {
        this.transactionID = transactionID;
        this.memberID = memberID;
        this.resourceID = resourceID;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = LoanStatus.CHECKED_OUT;
    }

    // checks if the transaction is overdue based on the current date
    public boolean isOverdue() {
        return new Date().after(dueDate) && status == LoanStatus.CHECKED_OUT;
    }

    // marks the resource as returned and sets the return date to now
    public void markReturned() {
        this.returnDate = new Date();
        this.status = LoanStatus.RETURNED;
    }

    // getter methods for all fields
    public String getTransactionID() { return transactionID; }
    public String getMemberID() { return memberID; }
    public String getResourceID() { return resourceID; }
    public Date getIssueDate() { return issueDate; }
    public Date getDueDate() { return dueDate; }
    public Date getReturnDate() { return returnDate; }
    public LoanStatus getStatus() { return status; }
}
