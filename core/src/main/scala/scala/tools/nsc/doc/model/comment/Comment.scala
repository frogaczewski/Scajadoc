/* NSC -- new Scala compiler -- Copyright 2007-2010 LAMP/EPFL */

package scala.tools.nsc
package doc
package model
package comment

import scala.collection._

/** A Scaladoc comment and all its tags.
  * 
  * '''Note:''' the only instantiation site of this class is in [[CommentFactory]].
  * 
  * @author Gilles Dubochet
  * @author Manohar Jonnalagedda */
abstract class Comment {
  
  /** The main body of the comment that describes what the entity does and is.  */
  def body: Body

  /** A shorter version of the body. Usually, this is the first sentence of the body. */
  def short: Inline = body.summary getOrElse Text("")
  
  /** A list of authors. The empty list is used when no author is defined. */
  def authors: List[Body]

  /** A list of other resources to see, including links to other entities or to external documentation. The empty list
    * is used when no other resource is mentionned. */
  def see: List[Body]
  
  /** A description of the result of the entity. Typically, this provides additional information on the domain of the
    * result, contractual post-conditions, etc. */
  def result: Option[Body]
  
  /** A map of exceptions that the entity can throw when accessed, and a description of what they mean. */
  def throws: Map[String, Body]
  
  /** A map of value parameters, and a description of what they are. Typically, this provides additional information on
    * the domain of the parameters, contractual pre-conditions, etc. */
  def valueParams: Map[String, Body]

  /** A map of type parameters, and a description of what they are. Typically, this provides additional information on
    * the domain of the parameters. */
  def typeParams: Map[String, Body]
  
  /** The version number of the entity. There is no formatting or further meaning attached to this value. */
  def version: Option[Body]
  
  /** A version number of a containing entity where this member-entity was introduced. */
  def since: Option[Body]
  
  /** An annotation as to expected changes on this entity. */
  def todo: List[Body]
  
  /** Whether the entity is deprecated. Using the "@deprecated" Scala attribute is prefereable to using this Scaladoc
    * tag. */
  def deprecated: Option[Body]

  /** An additional note concerning the contract of the entity. */
  def note: List[Body]

  /** A usage example related to the entity. */
  def example: List[Body]

  /** The comment as it appears in the source text. */
  def source: Option[String]
  
  /** A description for the primary constructor */
  def constructor: Option[Body]
  
  override def toString =
    body.toString + "\n" +
    (authors map ("@author " + _.toString)).mkString("\n") +
    (result map ("@return " + _.toString)).mkString("\n") +
    (version map ("@version " + _.toString)).mkString
  
}
