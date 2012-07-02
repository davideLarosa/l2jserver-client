package com.l2client.model.network;

/**
 * Central client data model for entity (dynamic, movable objects) data based on the underlying 
 * raw network data.
 * -Uses the updateFrom method to update certain field from a new EntityData object.
 * -Stores the server returned Z value for playback to the server, as this can get very ugly if not near the same value and no geodata is present to correct this.
 * -speed is currently scaled down by 50% as this will be interpreted as meters/second (or should it be feet per sec? -> *0.3)
 */
//TODO no paper doll model (extra)
//TODO check speed scaling
//TODO move over to l2j component
public class EntityData  {


	private String _name;
    private int _objectId = 0;
	private int _charId = 0;// 0x00030b7a;
	private long _exp =0;
	private int _sp =0;
	private int _clanId=0;
	private int _race=0;
	private int _classId=0;
    private int _baseClassId=0;
	private long _deleteTimer=0L;
	private long _lastAccess=0L;
	private int _face=0;
	private int _hairStyle=0;
	private int _hairColor=0;
	private int _sex=0;
	private int _level = 1;
	private double _maxHp=0;
	private double _currentHp=0;
	private double _maxMp=0;
	private double _currentMp=0;
//	private int[][] _paperdoll;
    private int _karma=0;
    private int _pkKills=0;
    private int _pvpKills=0;
    private int _augmentationId=0;
    private int _transformId = 0;
    private float _x = 0;
    private float _y = 0;
    private float _z = 0;
    private float _heading =0;
	private String _title;
	private boolean _GM;
	private float _walkSpeed;
	private float _runSpeed;
	private boolean _running = false;
	/**
	 * Raw Server based value of the height used to returning move requests
	 */
	private int _serverZ;



    public EntityData()
    {
    }

    public int getObjectId()
    {
        return _objectId;
    }

    public void setObjectId(int objectId)
    {
        _objectId = objectId;
    }

    public int getCharId()
    {
        return _charId;
    }
    public void setCharId(int charId)
    {
        _charId = charId;
    }
	public int getClanId()
	{
		return _clanId;
	}
	public void setClanId(int clanId)
	{
		_clanId = clanId;
	}
	public int getClassId()
	{
		return _classId;
	}
    public int getBaseClassId()
    {
        return _baseClassId;
    }
	public void setClassId(int classId)
	{
		_classId = classId;
	}
    public void setBaseClassId(int baseClassId)
    {
        _baseClassId = baseClassId;
    }
	public double getCurrentHp()
	{
		return _currentHp;
	}
	public void setCurrentHp(double currentHp)
	{
		_currentHp = currentHp;
	}
	public double getCurrentMp()
	{
		return _currentMp;
	}
	public void setCurrentMp(double currentMp)
	{
		_currentMp = currentMp;
	}
	public long getDeleteTimer()
	{
		return _deleteTimer;
	}
	public void setDeleteTimer(long deleteTimer)
	{
		_deleteTimer = deleteTimer;
	}
	public long getLastAccess()
	{
		return _lastAccess;
	}
	public void setLastAccess(long lastAccess)
	{
		_lastAccess = lastAccess;
	}
	public long getExp()
	{
		return _exp;
	}
	public void setExp(long exp)
	{
		_exp = exp;
	}
	public int getFace()
	{
		return _face;
	}
	public void setFace(int face)
	{
		_face = face;
	}
	public int getHairColor()
	{
		return _hairColor;
	}
	public void setHairColor(int hairColor)
	{
		_hairColor = hairColor;
	}
	public int getHairStyle()
	{
		return _hairStyle;
	}
	public void setHairStyle(int hairStyle)
	{
		_hairStyle = hairStyle;
	}
//	public int getPaperdollObjectId(int slot)
//	{
//		return _paperdoll[slot][0];
//	}
//	public int getPaperdollItemId(int slot)
//	{
//		return _paperdoll[slot][1];
//	}
	public int getLevel()
	{
		return _level;
	}
	public void setLevel(int level)
	{
		_level = level;
	}
	public double getMaxHp()
	{
		return _maxHp;
	}
	public void setMaxHp(double d)
	{
		_maxHp = d;
	}
	public double getMaxMp()
	{
		return _maxMp;
	}
	public void setMaxMp(double d)
	{
		_maxMp = d;
	}
	public String getName()
	{
		return _name;
	}
	public void setName(String name)
	{
		_name = name;
	}
	public int getRace()
	{
		return _race;
	}
	public void setRace(int race)
	{
		_race = race;
	}
	public int getSex()
	{
		return _sex;
	}
	public void setSex(int sex)
	{
		_sex = sex;
	}
	public int getSp()
	{
		return _sp;
	}
	public void setSp(int sp)
	{
		_sp = sp;
	}
//	public int getEnchantEffect()
//	{
//		if (_paperdoll[Inventory.PAPERDOLL_RHAND][2] > 0)
//			return _paperdoll[Inventory.PAPERDOLL_RHAND][2];
//        return _paperdoll[Inventory.PAPERDOLL_LRHAND][2];
//	}
    public void setKarma(int k)
    {
        _karma = k;
    }
    public int getKarma()
    {
        return _karma;
    }
    public void setAugmentationId(int augmentationId)
    {
    	_augmentationId = augmentationId;
    }
    public int getAugmentationId()
    {
    	return _augmentationId;
    }
    public void setPkKills(int PkKills)
    {
        _pkKills = PkKills;
    } 
    public int getPkKills()
    {
        return _pkKills;
    }
    public void setPvPKills(int PvPKills)
    {
        _pvpKills = PvPKills;
    } 
    public int getPvPKills()
    {
        return _pvpKills;
    }
    public int getTransformId() 
    { 
        return _transformId; 
    } 
    public void setTransformId(int id) 
    { 
        _transformId = id; 
    }
    public float getX()
    {
    	return _x;
    }
  //height will be always 0, we ignore it whatever comes from the server
    public float getY()
    {
    	return 0f;
    }
    public float getZ()
    {
    	return _z;
    }
    public void setX(float x)
    {
    	_x = x;
    }
    public void setY(float y)
    {
    	_y = y;
    }
    public void setZ(float z)
    {
    	_z = z;
    }

    public void setServerZ(int z){
    	_serverZ =z;
    }
    
	public int getServerZ() {
		return _serverZ;
	}

	public void setTitle(String title) {
		_title = title;
	}
	
	public String getTitle(){
		return _title;
	}

	/**
	 * Heading in radians
	 * @return
	 */
	public final float getHeading() {
		return _heading;
	}

	public final void setHeading(float _heading) {
		this._heading = _heading;
	}

	public void setGM(boolean b) {
		this._GM = b;
	}
	/**
	 * updates selected settings by overwriting:
	 * _x
	 * _y
	 * _z
	 * _currentHp
	 * _currentMp
	 * _title
	 * _speed
	 * _serverZ
	 * @param i the CharInfoPackage the values should be copied from
	 */
	public void updateFrom(EntityData i){
		if(_objectId == 0)
			_objectId = i.getObjectId();
		_x = i.getX();
		_y = i.getY();
		_z = i.getZ();
		_currentHp = i.getCurrentHp();
		_currentMp = i.getCurrentMp();
		_title = i.getTitle();
		_walkSpeed = i.getWalkSpeed();
		_runSpeed = i.getRunSpeed();
		_serverZ = i.getServerZ();
	}
	
	public void setWalkSpeed(float i){
		this._walkSpeed = i;
	}

	public float getWalkSpeed() {
		return _walkSpeed;
	}
	
	public void setRunSpeed(float i){
		this._runSpeed = i;
	}

	public float getRunSpeed() {
		return _runSpeed;
	}
	
	public void setRunning(boolean b) {
		this._running = b;
	}
	
	public boolean isRunning(){
		return _running;
	}

	public boolean isGM() {
		return _GM;
	}
}
