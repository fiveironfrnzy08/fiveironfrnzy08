package org.ryangray.onkyo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.gen2.picker.client.SliderBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.kiouri.sliderbar.client.solution.simplehorizontal.SliderBarSimpleHorizontal;
import com.smartgwt.client.widgets.HTMLFlow;
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Onkyo implements EntryPoint {

	private static Button						onButton;
	private static Button						offButton;
	private static SliderBarSimpleHorizontal	volumeSlider;
//	private static SliderBar					slider;
	private static boolean						waitingForReceiverResponse	= false;

	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String					SERVER_ERROR				= "An error occurred while " + "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final GreetingServiceAsync			greetingService				= GWT.create( GreetingService.class );

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad( ) {
//		SliderBar.injectDefaultCss();

		onButton = new Button( "Power On" );
		offButton = new Button( "Power Off" );
		volumeSlider = new SliderBarSimpleHorizontal( 64, "400px", false );
//		slider = new SliderBar( 0.0, 100.0 );
//		slider.setStepSize( 1.0 );
//		slider.setCurrentValue(50.0);
//		slider.setNumTicks(10);
//		slider.setNumLabels(5);
//		slider.setWidth( "400px" );
//		slider.setHeight( "40px" );
		HTMLFlow vlc = new HTMLFlow( "<!DOCTYPE html>"
				+ "<html>"
				+ "<head>"
				+ "	<title>My realtime chart</title>"
				+ "	<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>"
				+ "		<script type=\"text/javascript\" src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js\"></script>"
				+ "		<script type=\"text/javascript\">"
//				+ "			// Load the Visualization API and the piechart package."
				+ "			google.load('visualization', '1', {'packages':['corechart']});"
				
//				+ "			// Set a callback to run when the Google Visualization API is loaded."
				+ "			google.setOnLoadCallback(drawChart);"
				
				+ "			function drawChart() {"
				+ "				var json = $.ajax({"
				+ "					url: 'http://ryangray.org/airfare/getData.php?flight_id=15', "
				+ "					dataType: 'json',"
				+ "					async: false"
				+ "				}).responseText;"
				+ "				"
//				+ "				// Create our data table out of JSON data loaded from server."
				+ "				var data = new google.visualization.DataTable(json);"
				+ "				var options = {"
				+ "					title: 'My Weekly Plan',"
				+ "					is3D: 'true',"
				+ "					width: 800,"
				+ "					height: 600"
				+ "				};"
//				+ "				// Instantiate and draw our chart, passing in some options."
//				+ "				//do not forget to check ur div ID"
				+ "				var chart = new google.visualization.LineChart(document.getElementById('chart_div'));"
				+ "				chart.draw(data, options);"
				
				+ "				setInterval(drawChart, 500 );"
				+ "			}"
				+ "		</script>  "
				
				+ "</head>"
				
				+ "<body>"
				
				+ "	  "
				+ "	<div id=\"chart_div\" style=\"width: 500px; height: 500px;\"></div>"
				+ " "
				+ " "
				+ "</body>"
				+ "</html>" );
		
		Label errorLabel = new Label( );

		volumeSlider.addStyleName( "Volume" );
//		slider.addStyleName( "Volume" );
		onButton.addStyleName( "Turn on" );
		offButton.addStyleName( "Turn off" );

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get( "onButtonContainer" ).add( onButton );
		RootPanel.get( "offButtonContainer" ).add( offButton );
		RootPanel.get( "volumeSliderContainer" ).add( volumeSlider.asWidget( ) );
		RootPanel.get( "vlcContainer" ).add( vlc.asWidget( ) );
//	    RootPanel.get( "sliderContainer" ).add( slider );
		RootPanel.get( "errorLabelContainer" ).add( errorLabel );

		// Initialize receiver settings
		System.out.println( "waitingForReceiverResponse = true");
		waitingForReceiverResponse = true;
		sendCommandToReceiver( new String[] { "PWR", "QSTN" }, new String[] { "MVL", "QSTN" } );
		
		

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {

			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick( ClickEvent event ) {

				if ( onButton.equals( ( Button ) event.getSource( ) ) ) {

					if ( !waitingForReceiverResponse ) {
						System.out.println( "waitingForReceiverResponse = true");
						waitingForReceiverResponse = true;
						sendCommandToReceiver( new String[] { "PWR", "01" } );
					}

				} else if ( offButton.equals( ( Button ) event.getSource( ) ) ) {

					if ( !waitingForReceiverResponse ) {
						System.out.println( "waitingForReceiverResponse = true");
						waitingForReceiverResponse = true;
						sendCommandToReceiver( new String[] { "PWR", "00" } );
					}

				}

			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp( KeyUpEvent event ) {

				if ( event.getNativeKeyCode( ) == KeyCodes.KEY_ENTER ) {

					/**
					 * No specific functions for keyUP, so just handle as a
					 * click
					 */
					( ( Button ) event.getSource( ) ).click( );

				}

			}

		}

		onButton.addClickHandler( new MyHandler( ) );
		offButton.addClickHandler( new MyHandler( ) );
//		slider.addValueChangeHandler( new ValueChangeHandler< Double >( ) {
//
//			@Override
//			public void onValueChange( ValueChangeEvent< Double > event ) {
//				System.out.println( event.getClass( ).getName( ) );
//				int value = ( ( SliderBarSimpleHorizontal ) event.getSource( ) ).getValue( );
//				System.out.println( "BarValueChanged Volume: " + value );
//				String hex = Integer.toHexString( value );
//				if ( hex.length( ) == 1 ) {
//					hex = "0" + hex;
//				}
//				System.out.println( "Sending hex volume: " + hex );
//				System.out.println( "waitingForReceiverResponse = true");
//				waitingForReceiverResponse = true;
//				sendCommandToReceiver( new String[] { "MVL", hex } );
//			}
//		} );
		volumeSlider.addClickHandler( new ClickHandler( ) {
			
			@Override
			public void onClick( ClickEvent event ) {
				
				System.out.println( event.getClass( ).getName( ) );
				int value = ( ( SliderBarSimpleHorizontal ) event.getSource( ) ).getValue( );
				System.out.println( "BarValueChanged Volume: " + value );
				String hex = Integer.toHexString( value );
				if ( hex.length( ) == 1 ) {
					hex = "0" + hex;
				}
				System.out.println( "Sending hex volume: " + hex );
				System.out.println( "waitingForReceiverResponse = true");
				waitingForReceiverResponse = true;
				sendCommandToReceiver( new String[] { "MVL", hex } );
				
			}
		} );
		

	}

	/**
	 * Send the command from the button clicked to the server and wait for a
	 * response.
	 * 
	 * @return
	 */
	private void sendCommandToReceiver( String[]... command ) {

		final String[][] thisCommand = command;
		// Then, we send the input to the server.
		greetingService.greetServer( command,

		new AsyncCallback< String[] >( ) {

			public void onFailure( Throwable caught ) {

				System.out.println( "Async Failure!");
				System.out.println( "waitingForReceiverResponse = false");
				waitingForReceiverResponse = false;
			}

			public void onSuccess( String[] results ) {
				
				if ( results == null ) {
					
					/**
					 * Will occur if exception is thrown with connection to receiver. Reset fields so the program can continue.
					 */
					System.out.println( "NULL RETURNED");
					onButton.setEnabled( true );
					offButton.setEnabled( true );
					volumeSlider.setValue( 0 );
//					slider.setCurrentValue(50.0);
					return;
					
				}

				// for ( String result: results ) {
				for ( int i = 0; i < results.length; i++ ) {
					
					String result = results[ i ];
					
					System.out.println( "Results returned to client!" );
					System.out.println( result );

					if ( "PWR01".equals( result ) ) {

						onButton.setEnabled( false );
						offButton.setEnabled( true );

					} else if ( "PWR00".equals( result ) ) {

						onButton.setEnabled( true );
						offButton.setEnabled( false );

					} else if ( result.startsWith( "MVL" ) ) {
						
						if ( !"MVLNA".equals( result ) ) {
							int position = Integer.parseInt( result.substring( 3 ), 16 );
							System.out.println( "MVL hex returned: " + result + "; Parsed int value: " + position );
							volumeSlider.setValue( position );
						}

					}

				}

				System.out.println( "waitingForReceiverResponse = false");
				waitingForReceiverResponse = false;

			}

		} );

	}
	
	
}
