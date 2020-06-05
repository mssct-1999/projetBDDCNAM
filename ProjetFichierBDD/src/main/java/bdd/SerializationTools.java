package bdd;

import java.io.*;
import java.util.Arrays;
import java.util.TreeSet;

/**
 * Classe qui contient des outils de sérialization
 *
 * @author Jason Mahdjoub
 * @version 1.0
 */
class SerializationTools {
	/**
	 * Serialise/binarise l'objet passé en paramètre pour retourner un tableau binaire
	 * @param o l'objet à serialiser
	 * @return the tableau binaire
	 * @throws IOException si un problème d'entrée/sortie se produit
	 */
	static byte[] serialize(Serializable o) throws IOException {
		byte[] arrayBytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		if (o != null) {
			try {
				try {
					// création flux "objet"
					ObjectOutputStream oos = new ObjectOutputStream(bos);
					// sérialisation
					oos.writeObject(o);
					oos.flush();
					arrayBytes = bos.toByteArray();
					System.out.println(o + " a ete serialise");
				} finally {
					//fermeture
					try {
						bos.close();
					} catch(IOException e) {
						System.out.println("Erreur lors de la sérialisation - flux de sortie");
						e.printStackTrace();
					}
				}
			} catch(IOException e) {
				System.out.println("Erreur lors de la sérialisation de l'objet - flux d'entrée");
				e.printStackTrace();
			}
		} else {
			throw new NullPointerException();
		}
		return arrayBytes;
	}

	/**
	 * Désérialise le tableau binaire donné en paramètre pour retrouver l'objet initial avant sa sérialisation
	 * @param data le tableau binaire
	 * @return l'objet désérialisé
	 * @throws IOException si un problème d'entrée/sortie se produit
	 * @throws ClassNotFoundException si un problème lors de la déserialisation s'est produit
	 */
	static Serializable deserialize(byte[] data) throws IOException, ClassNotFoundException {
		Serializable o;
		if (data != null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			ObjectInput in = null;
			try {
				in = new ObjectInputStream(bis);
				o = (Serializable) in.readObject();
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch(IOException e) {
					System.out.println("Erreur lors de la désérialisation");
					e.printStackTrace();
				}
			}
		} else {
			throw new NullPointerException();
		}
		return o;
	}

	/**
	 * Serialise/binarise le tableau d'espaces libres passé en paramètre pour retourner un tableau binaire, mais selon le schéma suivant :
	 * Pour chaque interval ;
	 * <ul>
	 *     <li>écrire en binaire la position de l'interval</li>
	 *     <li>écrire en binaire la taille de l'interval</li>
	 * </ul>
	 * Utilisation pour cela la classe {@link DataOutputStream}
	 *
	 * @param freeSpaceIntervals le tableau d'espaces libres
	 * @return un tableau binaire
	 * @throws IOException si un problème d'entrée/sortie se produit
	 */
	static byte[] serializeFreeSpaceIntervals(TreeSet<BDD.FreeSpaceInterval> freeSpaceIntervals) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		if (freeSpaceIntervals != null) {
			try {
				for (BDD.FreeSpaceInterval spaceInterval : freeSpaceIntervals) {
					dos.writeLong(spaceInterval.getStartPosition());
					dos.writeLong(spaceInterval.getLength());
					dos.flush();
				}
			} catch(Exception e) {

			}
		} else {
			throw new NullPointerException();
		}
		return bos.toByteArray();
	}

	/**
	 * Effectue l'opération inverse de la fonction {@link #serializeFreeSpaceIntervals(TreeSet)}
	 * @param data le tableau binaire
	 * @return le tableau d'espaces libres
	 * @throws IOException si un problème d'entrée/sortie se produit
	 */
	static TreeSet<BDD.FreeSpaceInterval> deserializeFreeSpaceIntervals(byte[] data) throws IOException {
		TreeSet<BDD.FreeSpaceInterval> freeSpace = new TreeSet<BDD.FreeSpaceInterval>();
		if (data != null) {
			try {
				for (int i = 0; i < data.length; i++) {
					ByteArrayInputStream byteArray = new ByteArrayInputStream(Arrays.copyOfRange(data,i,i+8));
					DataInputStream obj = new DataInputStream(byteArray);
					ByteArrayInputStream byteArray2 = new ByteArrayInputStream(Arrays.copyOfRange(data, i + 8, i + 16));
					DataInputStream obj2 = new DataInputStream(byteArray2);
					freeSpace.add(new BDD.FreeSpaceInterval((long)byteArray.read(), (long)byteArray2.read()));
					byteArray.close();
					obj.close();
				}
			} catch(IOException io) {
				io.printStackTrace();
				System.err.println("Erreur - Méthode deserializeSpaceIntervals");
			}
		} else {
			throw new NullPointerException();
		}
		return freeSpace;
	}
}
