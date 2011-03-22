package org.scajadoc.distributed

import swing._
import event._

/**
 * Application entry point. 
 *
 * @author Filip Rogaczewski
 */
object DistributedAlgorithmsPresentation extends SimpleSwingApplication {

	/**
	 * Rows count for the main panel.
	 */
	final val rows = 10

	/**
	 * Columns count for the main panel.
	 */
	final val cols = 10

	/**
	 * List of algorithms supported by this presentation.
	 */
	val algorithms = List(DistributedAlgorithm("LCR", None),
		DistributedAlgorithm("HS", None),
		DistributedAlgorithm("ML", None))

	val algorithmsList = new ListView(algorithms) {
		renderer = ListView.Renderer(_.name)
	}

	def top = new MainFrame {
		title = "Distributed algorithms"
		
		contents = new GridPanel(rows, cols) {
			contents += algorithmsList
		}
		listenTo(algorithmsList.selection)
		reactions += {
			case ListSelectionChanged(source, range, live) => {
				try {
					Executor.execute(algorithms(source.selection.anchorIndex).algorithm)
				} catch {
					case ex : NoAlgorithmException => Dialog.showMessage(title = "Program exception",
						message = ex.getLocalizedMessage, messageType = Dialog.Message.Error)
				}
			}
		}
	}

}