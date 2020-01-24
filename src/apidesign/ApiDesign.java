package apidesign;

import database_connect.DB;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.Discount;

public class ApiDesign {
    private static Date currentDate = new Date();
    private static DateFormat df = new SimpleDateFormat("dd-MM-yy");
    private static Connection conn = null;
    
    public static Date getEventDate(String eventName) {
        Date result = null;
        try {
            String query = "select * from EVENT where Name='"+eventName+"'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next()) {
                result = rs.getDate("Date");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiDesign.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
    
    public static boolean isRegistredBuyer(String buyerName) {
        boolean result = false;
        try {
            String query = "select * from BOUGHT BO JOIN BUYER BU ON BO.PassB = BU.PassB WHERE BU.Name = '"+buyerName+"'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next()) {
               result = true; 
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiDesign.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public static List<Discount> getAllDiscounts() {
        List<Discount> discounts = new ArrayList<>();
        String query = "select * from DISCOUNT ORDER BY TicketsNumber ASC ";
        Statement stmt;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                Discount d = new Discount();
                d.setTicketsNumber(rs.getInt("TicketsNumber"));
                d.setDiscount(rs.getDouble("Discount"));
                discounts.add(d);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiDesign.class.getName()).log(Level.SEVERE, null, ex);
        }
        return discounts;
    }
    
    public static double computeDiscount(String buyerName) {
        int currentBoughtTickets = 0;
        double discount = 0.0;
        try {
            String query = "select count(*) AS counter from BOUGHT BO JOIN BUYER BU ON BO.PassB = BU.PassB WHERE BU.Name = '"+buyerName+"'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next()) {
                currentBoughtTickets = rs.getInt("counter");
            }else{
                currentBoughtTickets = 0;
                return discount;
            }
            
            List<Discount> discountList = getAllDiscounts();
            for(Discount d: discountList) {
                if(d.getTicketsNumber() <= currentBoughtTickets) {
                    discount = d.getDiscount();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiDesign.class.getName()).log(Level.SEVERE, null, ex);
        }
        return discount;
    }
    
    public static void buyValidTicket(int ticketPass,String buyerName) {
        try {
            String queryUpdateTicket = "update TICKET set Status = 'P' where PassT="+ticketPass;
            String queryInsertBought = "insert BOUGHT values ("+ticketPass+","+getBuyerIdByName(buyerName)+",0)";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(queryUpdateTicket);
            stmt.executeUpdate(queryInsertBought);
        } catch (SQLException ex) {
            Logger.getLogger(ApiDesign.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static int getBuyerIdByName(String buyerName) {
        int resultId = -1;
        try {
            String query = "select PassB FROM BUYER where Name='"+buyerName+"'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next()) {
                resultId = rs.getInt("PassB");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiDesign.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return resultId; // resultId = -1 means unknown user
    }
    
    public static void  buyValidTickets(String buyerName, String eventName, int ticketsNum) {
        try {
            String query = "select TICKET.PassT FROM TICKET JOIN VALID ON TICKET.PassT = VALID.PassT JOIN EVENT ON VALID.PassE = EVENT.PassE WHERE TICKET.Status='S'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
               if(ticketsNum > 0) {
                   buyValidTicket(rs.getInt("PassT"),buyerName);
                   ticketsNum--;
               } 
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApiDesign.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sellingTicketsBeforeEvent(String buyerName, String eventName, int ticketsNum) {
        if(currentDate.after(getEventDate(eventName))) {
            System.out.println("We will not sell tickets after event day");
            return;
        }
        
        if(ticketsNum > 3) {
            System.out.println("One user can buy max 3 tickets");
            return;
        }
        
        if(!isRegistredBuyer(buyerName)) {
           System.out.println("This user is not registred!He cannot buy ticket before event day!");
           return;
        }
        
        double discount = computeDiscount(buyerName);
        buyValidTickets(buyerName,eventName,ticketsNum);
    }
    
    
    public static void main(String[] args) throws SQLException {
        DB db = new DB();
        conn = db.initDB();
        System.out.println("Database successfuly initlaized!");
        
        sellingTicketsBeforeEvent("B1","D1",5);
        sellingTicketsBeforeEvent("B1","D1",3); 
    }
}
