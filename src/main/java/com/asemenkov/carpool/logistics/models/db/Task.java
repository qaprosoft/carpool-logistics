package com.asemenkov.carpool.logistics.models.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Proxy;

/**
 * @author asemenkov
 * @since Feb 14, 2018
 */
@Entity
@Proxy(lazy = false)
@Table(name = "TASKS")
@NamedEntityGraph(name = "taskEntityGraph", attributeNodes = //
{ @NamedAttributeNode("user"), @NamedAttributeNode("hub") })
public class Task implements Serializable {

	private static final long serialVersionUID = 3864329823381194210L;

	public enum Status {
		UNASSIGNED, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "HUB_ID", nullable = false)
	private Hub hub;

	@ManyToOne
	@JoinColumn(name = "PASSENGER_ID", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS", nullable = false)
	private Status status;

	@Column(name = "TRIP_ID", nullable = true)
	private Long tripId;

	@Column(name = "SHIFT_ID", nullable = false)
	private Long shiftId;

	@Column(name = "PICKUP_TIME", nullable = true)
	private Date pickupTime;

	@Column(name = "PICKUP_ADDRESS", nullable = false)
	private String pickupAddress;

	@Formula("ST_ASTEXT(PICKUP_LOCATION)")
	private String pickupLocation;

	@Column(name = "DROPOFF_TIME", nullable = true)
	private Date dropoffTime;

	@Column(name = "DROPOFF_ADDRESS", nullable = false)
	private String dropoffAddress;

	@Formula("ST_ASTEXT(DROPOFF_LOCATION)")
	private String dropoffLocation;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Hub getHub() {
		return hub;
	}

	public void setHub(Hub hub) {
		this.hub = hub;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Long getTripId() {
		return tripId;
	}

	public void setTripId(Long tripId) {
		this.tripId = tripId;
	}

	public Long getShiftId() {
		return shiftId;
	}

	public void setShiftId(Long shiftId) {
		this.shiftId = shiftId;
	}

	public Date getPickupTime() {
		return pickupTime;
	}

	public void setPickupTime(Date pickupTime) {
		this.pickupTime = pickupTime;
	}

	public String getPickupAddress() {
		return pickupAddress;
	}

	public void setPickupAddress(String pickupAddress) {
		this.pickupAddress = pickupAddress;
	}

	public String getPickupLocation() {
		return pickupLocation;
	}

	public void setPickupLocation(String pickupLocation) {
		this.pickupLocation = pickupLocation;
	}

	public Date getDropoffTime() {
		return dropoffTime;
	}

	public void setDropoffTime(Date dropoffTime) {
		this.dropoffTime = dropoffTime;
	}

	public String getDropoffAddress() {
		return dropoffAddress;
	}

	public void setDropoffAddress(String dropoffAddress) {
		this.dropoffAddress = dropoffAddress;
	}

	public String getDropoffLocation() {
		return dropoffLocation;
	}

	public void setDropoffLocation(String dropoffLocation) {
		this.dropoffLocation = dropoffLocation;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
}
