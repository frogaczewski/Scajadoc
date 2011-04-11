package org.scajadoc.distributed;

import java.util.StringTokenizer;

/**
 * // TODO: Document this
 *
 * @author Filip
 * @since 4.0
 */
public class bytes {

   public static void main(String... args) {
      String input = "4 3C 4 3D 1B 1C 60 91 3E 1D AC";
      StringTokenizer tokenizer = new StringTokenizer(input);
      while (tokenizer.hasMoreElements()) {
         String token = tokenizer.nextToken();
         byte b = (byte) Integer.parseInt(token, 16);
         System.out.print((char) b);
      }
   }


}
