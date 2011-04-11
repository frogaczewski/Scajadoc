package org.scajadoc.distributed;

import scala.Option;

import javax.swing.text.html.parser.Entity;

public interface Extractor<T extends Entity, X extends Extract> {

   Option<X> extract(T t);

}

class Extract {}
