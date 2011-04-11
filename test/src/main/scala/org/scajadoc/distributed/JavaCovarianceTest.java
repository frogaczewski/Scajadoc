package org.scajadoc.distributed;

import java.util.ArrayList;
import java.util.List;

/**
 * // TODO: Document this
 *
 * @author Filip
 * @since 4.0
 */
public class JavaCovarianceTest {

   public static void main(String... args) {
      List<String> list = new ArrayList<String>();
      if (list instanceof List<?>)
         System.out.println(true);
//      System.out.println(list insanceof List<Object>);
   }

}
