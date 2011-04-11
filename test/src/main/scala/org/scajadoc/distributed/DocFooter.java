package org.scajadoc.distributed;

import java.awt.*;
import java.applet.*;

public class DocFooter extends Applet {
     String date;
     String email;

     public void init() {
             resize(500,100);
             date = getParameter("LAST_UPDATED");
             email = getParameter("EMAIL");
     }

     public void paint(Graphics g) {
             g.drawString(date + " by ",100, 15);
             g.drawString(email,290,15);
     }
}


