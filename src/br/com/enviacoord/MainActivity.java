package br.com.enviacoord;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.enviacoord.HttpClient;
import br.com.enviacoord.R;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener {

	private Location local ;
	private Handler handler ;
	private boolean controle ; //Variável de controle de execução da Runnable
	private TextView txtStatus ; 
	private Button btnLiga ;
	
	Runnable run = new Runnable(){
		public void run(){
    		if(controle){
    			mostrarCoord();
    			checkPedido() ;
    			handler.postDelayed(this, 5000) ;
    		}
    	} //Fecha run
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        txtStatus = (TextView) findViewById(R.id.txtStatus) ;
        txtStatus.setText(R.string.envioOn) ;
        
        handler = new Handler() ;
        controle = false ;
        //Cria novo objeto Runnable para ser executado em segundo plano
        handler.post(run) ;
    }//Fecha onCreate
    
    public void onClick(View v){
    	btnLiga = (Button) findViewById(R.id.btnLiga) ;
    	switch(v.getId()){
    	case R.id.btnLiga: 
    		if(controle){
    			controle = false ;
        		txtStatus.setText(R.string.envioOff) ;
        		btnLiga.setText("Ligar") ;
    		}else{
    			controle = true ;
        		txtStatus.setText(R.string.envioOn) ;
        		btnLiga.setText(R.string.txtDesligar) ;
        		handler.post(run) ;
    		}
    		break ;
    	}//Fecha switch
    }//Fecha onClick
    
    //Método que cria objeto JSON, acessa a WS e retorna dados recebidos
    public void mostrarCoord(){

    	HashMap params = getCoordenadas() ;
    	
    	//Cria-se o objeto JSON a partir do HashMap
        JSONObject jsonParams = new JSONObject(params);
        
        //Obtém-se a 'reposta' da WebService, defindo o método a ser acessado e os parâmetros
    	JSONObject resp = HttpClient.SendHttpPost(this.getString(R.string.urlMostrarCoord), jsonParams);
    	
    	//Doubles para exibição das coordenadas
    	double latExib = 0, lngExib =0;
    	String placaExib = "" ;
    	
    	//Obtém-se como resposta da WS as mesmas coordenadas enviadas
    	try {
			latExib = resp.getDouble("coord1");
			lngExib = resp.getDouble("coord2") ;
			placaExib = resp.getString("placa") ;
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	//Exibe na tela a mensagem com as coordenadas recebidas da WS
    	Toast.makeText(this, "Lat: "+latExib + " Lng:"+lngExib+ " Placa:" +placaExib, Toast.LENGTH_SHORT).show() ;
    	
    }//Fecha mostrarCoord
    
    public void checkPedido(){
    	
    	try{
    		
    		HashMap params = getCoordenadas() ;
        	JSONObject jsonParams = new JSONObject(params) ;
        	JSONObject resposta = HttpClient.SendHttpPost(this.getString(R.string.urlCheckPedido), jsonParams);
    		boolean pedido = resposta.getBoolean("Pedido") ;
    		
    		if(pedido){
    			String endereco = resposta.getString("Endereco") ;
        		String referencia = resposta.getString("Referencia") ;
        		
        		Intent it = new Intent(this, PedidoActivity.class) ;
            	Bundle extras = new Bundle() ;
            	extras.putString("Endereco", endereco) ;
            	extras.putString("Referencia", referencia) ;
            	it.putExtras(extras) ;
            	startActivity(it) ;
    		}
        	
    	}catch(JSONException e){
    		e.printStackTrace() ;
    	}
    	
    }//Fecha checkPedido
    
    public HashMap getCoordenadas(){
    	
    	//Doubles para receber coordenadas do GPS
    	double lat = 0, lng = 0 ; 
    	String placa = "AKZ-0989" ;
    	
    	//HashMap que irá receber os parâmetros que serão covertidos em JSON
    	HashMap params = new HashMap();
    	
    	//Cria o LocationManager
    	LocationManager LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	LM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        String bestProvider = LM.getBestProvider(new Criteria(), true) ;
        
        //Se obtém o local mais recente marcado pelo GPS
        local = LM.getLastKnownLocation(bestProvider) ;
        
        //Obtém-se os valores de latitude e longitude
        lat = local.getLatitude() ;
        lng = local.getLongitude();
        
        //Valores são inseridos no HashMap
        params.put("latitude", lat);
    	params.put("longitude", lng);
    	params.put("placa", placa) ;
    	
    	return params ;
    	
    }//Fecha getCoordenadas
    
    /*MÉTODOS IMPLEMENTADOS PARA LOCATION LISTENER**/
	@Override
	public void onLocationChanged(Location location) {
		this.local = location ;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		controle = false ;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		controle = true ;
		handler.post(run) ;
	}
	
	protected void onDestroy(){
		super.onDestroy();
		controle = false ;
	}

}
