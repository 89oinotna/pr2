package progettopr2;

import java.util.Iterator;

public class Example {
	
	static boolean hash_table = false; //True per testare la seconda implementazione
	static String[] testUsers = {"Antonio", "Bruno", "Carmine"};
	static String[] testPassw = {"a", "b", "c"};
	static int n = 3;
	
	public static void main(String[] args) {
		
		SecureDataContainer<String> container = (!hash_table)?(new MySecureDataContainer<String>()):(new MySecureDataContainerHT<String>());


		System.out.println("Inizio Test SecureDataContainer su "+((!hash_table)?"MySecureDataContainer":"MySecureDataContainerHT"));
		if(!hash_table)
			assert container instanceof MySecureDataContainer;
		else
			assert container instanceof MySecureDataContainerHT;

		/*
			TEST inserimento degli utenti:
		 */
		for (int i = 0; i < n; i++) {
			try {
				container.createUser(testUsers[i], testPassw[i]);
			}
			catch (DuplicateException e){
				e.printStackTrace();    //non viene mai sollevata
			}
		}
		System.out.println();

		

			//contatori per le eccezioni volutamente sollevate
			int exceptionsThrowed = 0;


			/*
				TEST METODO PUT
			 */
			container.put("Antonio", "a", "Acqua");
			container.put("Antonio", "a", "Vino");
			container.put("Antonio", "a", "Vodka");
			assert container.getSize("Antonio", "a") == 3;
			assert container.get("Antonio", "a", "Acqua").equals("Acqua");
			assert container.get("Antonio", "a", "Vino").equals("Vino");
			assert container.get("Antonio", "a", "Vodka").equals("Vodka");
			try{
				container.get("Antonio", "a", "CocaCola");
			}
			catch(DataNotFoundException e){
				exceptionsThrowed++;        //eccezione previsa, Antonio non contiene "CocaCola"
			}
			assert exceptionsThrowed == 1;


			/*
				TEST METODO REMOVE
			 */
			container.put("Bruno", "b", "Limonata");
			container.put("Bruno", "b", "Sprite");
			container.put("Bruno", "b", "Tequila");
			assert container.getSize("Bruno", "b") == 3;

			container.remove("Bruno", "b", "Limonata");
			assert container.getSize("Bruno", "b") == 2;
			
			
			try{
				container.remove("Bruno", "b", "Chinotto");
			}
			catch(DataNotFoundException e){
				exceptionsThrowed++;
			}
			assert container.getSize("Bruno", "b") == 2;
			assert exceptionsThrowed == 2;

			/*
				TEST METODO SHARE
			 */
			container.share("Antonio", "a", "Carmine", "Vino");
			container.share("Bruno", "b", "Carmine", "Sprite");
			assert container.getSize("Carmine", "c") == 0;
			assert container.get("Carmine", "c", "Vino") == "Vino";
			assert container.getSize("Antonio", "a") == 3;                //la size di Antonio non cambia

			container.share("Antonio", "a", "Bruno", "Vino");   //"Vino", già condiviso fra Antonio e Carmine, viene condiviso anche con Bruno
			assert container.getSize("Bruno", "b") == 2;
			assert container.get("Bruno", "b", "Vino") == "Vino";
			try{
				container.share("Bruno", "b", "Carmine", "Vino"); //Bruno non può condividere un dato del quale non è proprietario
			}
			catch(DataNotFoundException e){
				exceptionsThrowed++;
			}
			assert exceptionsThrowed == 3;
			
			/*
			TEST METODO REMOVE SHARE
			 */
			container.removeShare("Antonio", "a", "Carmine", "Vino");
			
			try{
				assert container.get("Carmine", "c", "Vino") == "Vino"; //Carmine non ha più "Vino" tra i condivisi
			}
			catch(DataNotFoundException e){
				exceptionsThrowed++;
			}
			assert container.get("Antonio", "a", "Vino") == "Vino"; //Antonio continua ad avere "Vino"
			assert exceptionsThrowed == 4;


			/*
				TEST METODO REMOVE
			 */			
			container.remove("Antonio", "a", "Vino");                 //Antonio rimuove "Vino", rimuovendolo anche per tutti gli utenti con cui era condiviso
			try{
				container.get("Antonio", "a", "Vino");
				
			}
			catch(DataNotFoundException e){
				exceptionsThrowed++;
			}
			assert exceptionsThrowed == 5;
			
			try{
				container.get("Bruno", "b", "Vino");
			}
			catch(DataNotFoundException e){
				exceptionsThrowed++;
			}
			assert exceptionsThrowed == 6;

			
			/*
				TEST METODO COPY
			 */

			container.copy("Carmine", "c", "Sprite"); 
			assert container.getSize("Carmine", "c") == 1;
			try{
				container.copy("Antonio", "a", "Sprite");
			}
			catch(DataNotFoundException e){
				exceptionsThrowed++;
			}
			assert exceptionsThrowed == 7;
			
			try{
				container.get("Antonio", "a", "Sprite");
			}
			catch(DataNotFoundException e){
				exceptionsThrowed++;
			}
			assert exceptionsThrowed == 8;


			/*
				TEST METODO GETITERATOR
			 */
			
			System.out.println("__________________");
			for (int i = 0; i < n; i++) {
				Iterator<String> itr=container.getIterator(testUsers[i], testPassw[i]);
				System.out.println("\n"+testUsers[i]+":");
				while(itr.hasNext()){
					System.out.println(itr.next());
				}
			}
			System.out.println("\n__________________");
		

	}
}
