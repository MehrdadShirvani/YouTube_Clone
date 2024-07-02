package Shared.Models;

import jakarta.persistence.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "Accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountId")
    private Long accountId;

    @Column(name = "ChannelId")
    private Long channelId;

    @Column(name = "FirstName", nullable = false, length = 50)
    private String firstName;

    @Column(name = "LastName", length = 50)
    private String lastName;

    @Column(name = "Username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "Email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "Password", nullable = false, columnDefinition = "TEXT")
    private String password;

    @Column(name = "BirthDate")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @Column(name = "CreatedDateTime", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdDateTime;

    @Column(name = "LastSeenAdDateTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp lastSeenAdDateTime;

    @Column(name = "PremiumExpirationDate")
    @Temporal(TemporalType.DATE)
    private Date premiumExpirationDate;

//    @ManyToOne
//    @JoinColumn(name = "ChannelId", insertable = false, updatable = false)
//    private Channel channel;

    public Account()
    {

    }
    public Account(String firstName, String lastName, String username, String email, String password, Date birthDate)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.createdDateTime = new Timestamp(System.currentTimeMillis());
        this.lastSeenAdDateTime = null;
        this.premiumExpirationDate = null;
    }

    public Long getAccountId() {
        return accountId;
    }
    public Long getChannelId() {
        return channelId;
    }
    public void setChannelId(Long channelId) {
        this.channelId = channelId;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Timestamp birthDate) {
        this.birthDate = birthDate;
    }

    public Timestamp getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Timestamp createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Timestamp getLastSeenAdDateTime() {
        return lastSeenAdDateTime;
    }

    public void setLastSeenAdDateTime(Timestamp lastSeenAdDateTime) {
        this.lastSeenAdDateTime = lastSeenAdDateTime;
    }

    public Date getPremiumExpirationDate() {
        return premiumExpirationDate;
    }

    public void setPremiumExpirationDate(Timestamp premiumExpirationDate) {
        this.premiumExpirationDate = premiumExpirationDate;
    }

//    public Channel getChannel() {
//        return channel;
//    }
}