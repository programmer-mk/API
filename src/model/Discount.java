/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

public class Discount {
    private int TicketsNumber;
    private double Discount; 

    public int getTicketsNumber() {
        return TicketsNumber;
    }

    public void setTicketsNumber(int TicketsNumber) {
        this.TicketsNumber = TicketsNumber;
    }

    public double getDiscount() {
        return Discount;
    }

    public void setDiscount(double Discount) {
        this.Discount = Discount;
    }
    
}
