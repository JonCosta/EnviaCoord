package br.com.enviacoord;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.app.Activity;
import android.content.Intent;

public class PedidoActivity extends Activity {

	private EditText txtEndereco, txtReferencia ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedido);
        
        txtEndereco = (EditText) findViewById(R.id.editEnd) ;
        txtReferencia = (EditText) findViewById(R.id.editRef) ;
        
        Intent it = getIntent() ;
        if(it != null){
        	Bundle params = it.getExtras() ;
        	if(params != null){
        		
        		String endereco = params.getString("Endereco") ;
        		String referencia = params.getString("Referencia") ;
        		txtEndereco.setText(endereco) ;
        		txtReferencia.setText(referencia) ;
        		
        	}
        }
        
    }//Fecha onCreate
    
    public void onClick(View v){
    	
    	switch(v.getId()){
    	case R.id.btnAceitar:
    		break ;
    	case R.id.btnNegar:
    		Intent it = new Intent(this, MainActivity.class) ;
    		startActivity(it) ;
    		break ;
    	}//Fecha switch
    	
    }//Fecha onClick

}//Fecha Activity