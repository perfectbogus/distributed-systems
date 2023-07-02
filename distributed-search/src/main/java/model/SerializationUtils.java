package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class SerializationUtils {
  public static byte[] serialize(Object object) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutput objectOutput = null;
    try {
      objectOutput = new ObjectOutputStream(byteArrayOutputStream);
      objectOutput.writeObject(object);
      objectOutput.flush();
      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Object deserialize(byte[] data) {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
    ObjectInput objectInput = null;
    try {
      objectInput = new ObjectInputStream(byteArrayInputStream);
      return objectInput.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}