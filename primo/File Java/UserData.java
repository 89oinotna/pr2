package progettopr2;

import java.util.Hashtable;


public class UserData<E>{
	// OVERVIEW: Tipo di dato che rappresenta un oggetto di tipo E e 
	// 			una collezione di utenti modificabile con cui è condiviso
    
	/* Typical Element: <obj, {shared_id_0, ..,shared_id_n-1}>
		 dove obj è un oggetto di tipo E, 
		 shared_id i è un oggetto di tipo String che rappresenta 
		 			gli id con i quali è condiviso obj
	  
	   Rep Invariant:
       RI(c) =  	c.obj != null
               && c.shared_id != null
               && for all i t.c. 0<=i<c.shared_id.values().toArray().lenght() => 
               			c.shared_id.values().toArray()[i]!=null
               && for all i,j t.c. 0<=i<j<c.shared_id.values().toArray().lenght() => 			//Id distini
               			c.shared_id.values().toArray()[i]!=c.shared_id.values().toArray()[j]
             
       Abstraction Function
       AF(c) = <c.obj, {c.shared_id.values().toArray()[i] | 0 <= i < c.shared_id.values().toArray().lenght() }>
	*/
	private E obj;
	private Hashtable<String, String> shared_id;


	
	public UserData(E data){
		/*EFFECTS: Inizializza this con < data, {} >.
		 */
		obj=data;
		shared_id = new Hashtable<String, String>();
		
	}
	
	//Restituisce obj di tipo E
	public E getObj() {
		/* EFFECTS: Restituisce this.obj
		 */
		return obj;
	}
	
	//Aggiunge id agli utenti con cui è condiviso obj
	public void addShare(String id){
	/* MODIFIES: this
	 * EFFECTS: Aggiunge id alla lista degli username con i quali è condiviso obj
	 */
		
		shared_id.put(id, id);
	}
	
	//Rimuove id dagli utenti con cui è condiviso obj
	public void removeShare(String id) {
		/* MODIFIES: this
		 * EFFECTS: Rimuove id dalla lista di utenti con cui è condiviso obj
		 * 			se id non è presente non fa nulla
		 */
		shared_id.remove(id);
	}
	
	
	//Restituisce gli user con cui è condiviso obj
	public Hashtable<String, String> getSharedList(){
		/*EFFECTS: Restituisce un oggetto Hashtable<String, String> che usa come 
		 * 			chiavi e come dati gli id degli utenti con cui è condiviso obj
		 */
		return shared_id;
		
	}
	
	
	
}