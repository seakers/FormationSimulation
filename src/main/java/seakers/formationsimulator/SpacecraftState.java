/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package seakers.formationsimulator;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
public class SpacecraftState implements org.apache.thrift.TBase<SpacecraftState, SpacecraftState._Fields>, java.io.Serializable, Cloneable, Comparable<SpacecraftState> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("SpacecraftState");

  private static final org.apache.thrift.protocol.TField POSITION_FIELD_DESC = new org.apache.thrift.protocol.TField("position", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField VELOCITY_FIELD_DESC = new org.apache.thrift.protocol.TField("velocity", org.apache.thrift.protocol.TType.STRUCT, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new SpacecraftStateStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new SpacecraftStateTupleSchemeFactory();

  public @org.apache.thrift.annotation.Nullable Vector3D position; // required
  public @org.apache.thrift.annotation.Nullable Vector3D velocity; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    POSITION((short)1, "position"),
    VELOCITY((short)2, "velocity");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // POSITION
          return POSITION;
        case 2: // VELOCITY
          return VELOCITY;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.POSITION, new org.apache.thrift.meta_data.FieldMetaData("position", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRUCT        , "Position")));
    tmpMap.put(_Fields.VELOCITY, new org.apache.thrift.meta_data.FieldMetaData("velocity", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRUCT        , "Velocity")));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(SpacecraftState.class, metaDataMap);
  }

  public SpacecraftState() {
  }

  public SpacecraftState(
    Vector3D position,
    Vector3D velocity)
  {
    this();
    this.position = position;
    this.velocity = velocity;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public SpacecraftState(SpacecraftState other) {
    if (other.isSetPosition()) {
      this.position = new Vector3D(other.position);
    }
    if (other.isSetVelocity()) {
      this.velocity = new Vector3D(other.velocity);
    }
  }

  public SpacecraftState deepCopy() {
    return new SpacecraftState(this);
  }

  @Override
  public void clear() {
    this.position = null;
    this.velocity = null;
  }

  @org.apache.thrift.annotation.Nullable
  public Vector3D getPosition() {
    return this.position;
  }

  public SpacecraftState setPosition(@org.apache.thrift.annotation.Nullable Vector3D position) {
    this.position = position;
    return this;
  }

  public void unsetPosition() {
    this.position = null;
  }

  /** Returns true if field position is set (has been assigned a value) and false otherwise */
  public boolean isSetPosition() {
    return this.position != null;
  }

  public void setPositionIsSet(boolean value) {
    if (!value) {
      this.position = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public Vector3D getVelocity() {
    return this.velocity;
  }

  public SpacecraftState setVelocity(@org.apache.thrift.annotation.Nullable Vector3D velocity) {
    this.velocity = velocity;
    return this;
  }

  public void unsetVelocity() {
    this.velocity = null;
  }

  /** Returns true if field velocity is set (has been assigned a value) and false otherwise */
  public boolean isSetVelocity() {
    return this.velocity != null;
  }

  public void setVelocityIsSet(boolean value) {
    if (!value) {
      this.velocity = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case POSITION:
      if (value == null) {
        unsetPosition();
      } else {
        setPosition((Vector3D)value);
      }
      break;

    case VELOCITY:
      if (value == null) {
        unsetVelocity();
      } else {
        setVelocity((Vector3D)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case POSITION:
      return getPosition();

    case VELOCITY:
      return getVelocity();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case POSITION:
      return isSetPosition();
    case VELOCITY:
      return isSetVelocity();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof SpacecraftState)
      return this.equals((SpacecraftState)that);
    return false;
  }

  public boolean equals(SpacecraftState that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_position = true && this.isSetPosition();
    boolean that_present_position = true && that.isSetPosition();
    if (this_present_position || that_present_position) {
      if (!(this_present_position && that_present_position))
        return false;
      if (!this.position.equals(that.position))
        return false;
    }

    boolean this_present_velocity = true && this.isSetVelocity();
    boolean that_present_velocity = true && that.isSetVelocity();
    if (this_present_velocity || that_present_velocity) {
      if (!(this_present_velocity && that_present_velocity))
        return false;
      if (!this.velocity.equals(that.velocity))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetPosition()) ? 131071 : 524287);
    if (isSetPosition())
      hashCode = hashCode * 8191 + position.hashCode();

    hashCode = hashCode * 8191 + ((isSetVelocity()) ? 131071 : 524287);
    if (isSetVelocity())
      hashCode = hashCode * 8191 + velocity.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(SpacecraftState other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetPosition()).compareTo(other.isSetPosition());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPosition()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.position, other.position);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetVelocity()).compareTo(other.isSetVelocity());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetVelocity()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.velocity, other.velocity);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("SpacecraftState(");
    boolean first = true;

    sb.append("position:");
    if (this.position == null) {
      sb.append("null");
    } else {
      sb.append(this.position);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("velocity:");
    if (this.velocity == null) {
      sb.append("null");
    } else {
      sb.append(this.velocity);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class SpacecraftStateStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public SpacecraftStateStandardScheme getScheme() {
      return new SpacecraftStateStandardScheme();
    }
  }

  private static class SpacecraftStateStandardScheme extends org.apache.thrift.scheme.StandardScheme<SpacecraftState> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, SpacecraftState struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // POSITION
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.position = new Vector3D();
              struct.position.read(iprot);
              struct.setPositionIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // VELOCITY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.velocity = new Vector3D();
              struct.velocity.read(iprot);
              struct.setVelocityIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, SpacecraftState struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.position != null) {
        oprot.writeFieldBegin(POSITION_FIELD_DESC);
        struct.position.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.velocity != null) {
        oprot.writeFieldBegin(VELOCITY_FIELD_DESC);
        struct.velocity.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class SpacecraftStateTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public SpacecraftStateTupleScheme getScheme() {
      return new SpacecraftStateTupleScheme();
    }
  }

  private static class SpacecraftStateTupleScheme extends org.apache.thrift.scheme.TupleScheme<SpacecraftState> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, SpacecraftState struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetPosition()) {
        optionals.set(0);
      }
      if (struct.isSetVelocity()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetPosition()) {
        struct.position.write(oprot);
      }
      if (struct.isSetVelocity()) {
        struct.velocity.write(oprot);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, SpacecraftState struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.position = new Vector3D();
        struct.position.read(iprot);
        struct.setPositionIsSet(true);
      }
      if (incoming.get(1)) {
        struct.velocity = new Vector3D();
        struct.velocity.read(iprot);
        struct.setVelocityIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

