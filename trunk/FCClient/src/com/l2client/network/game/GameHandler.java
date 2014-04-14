package com.l2client.network.game;

import java.util.logging.Level;

import com.l2client.network.game.ClientPackets.Logout;
import com.l2client.network.game.ClientPackets.ProtocolVersion;
import com.l2client.network.game.ServerPackets.AcquireSkillList;
import com.l2client.network.game.ServerPackets.ActionFailed;
import com.l2client.network.game.ServerPackets.Attack;
import com.l2client.network.game.ServerPackets.AutoAttackStart;
import com.l2client.network.game.ServerPackets.AutoAttackStop;
import com.l2client.network.game.ServerPackets.ChangeMoveType;
import com.l2client.network.game.ServerPackets.CharCreateFail;
import com.l2client.network.game.ServerPackets.CharCreateOk;
import com.l2client.network.game.ServerPackets.CharInfo;
import com.l2client.network.game.ServerPackets.CharSelected;
import com.l2client.network.game.ServerPackets.CharSelectionInfo;
import com.l2client.network.game.ServerPackets.CreatureSay;
import com.l2client.network.game.ServerPackets.DeleteObject;
import com.l2client.network.game.ServerPackets.Die;
import com.l2client.network.game.ServerPackets.DropItem;
import com.l2client.network.game.ServerPackets.ExBrExtraUserInfo;
import com.l2client.network.game.ServerPackets.GameServerPacket;
import com.l2client.network.game.ServerPackets.ItemList;
import com.l2client.network.game.ServerPackets.KeyPacket;
import com.l2client.network.game.ServerPackets.MoveToLocation;
import com.l2client.network.game.ServerPackets.MoveToPawn;
import com.l2client.network.game.ServerPackets.MyTargetSelected;
import com.l2client.network.game.ServerPackets.NpcInfo;
import com.l2client.network.game.ServerPackets.Revive;
import com.l2client.network.game.ServerPackets.ServerClose;
import com.l2client.network.game.ServerPackets.ShortCutInit;
import com.l2client.network.game.ServerPackets.SkillList;
import com.l2client.network.game.ServerPackets.SocialAction;
import com.l2client.network.game.ServerPackets.SpawnItem;
import com.l2client.network.game.ServerPackets.StartRotation;
import com.l2client.network.game.ServerPackets.StatusUpdate;
import com.l2client.network.game.ServerPackets.StopMove;
import com.l2client.network.game.ServerPackets.SystemMessage;
import com.l2client.network.game.ServerPackets.TargetSelected;
import com.l2client.network.game.ServerPackets.TargetUnselected;
import com.l2client.network.game.ServerPackets.TeleportToLocation;
import com.l2client.network.game.ServerPackets.UserInfo;
import com.l2client.network.game.ServerPackets.ValidateLocation;

/**
 * A concrete implementation of a L2J GameHandler. handlePacket creates
 * the L2J corresponding server packets where the packet data will be processed.
 * onConnect sends the initial ProtocolVersion packet for initialization game server
 * connection
 * onDisconnect tries to send the Logout packet for a graceful disconnection from the game server
 * 
 * everything else is handled in the individual server packets.
 * 
 *
 */
public class GameHandler extends BaseGameHandler {

	/**
	 * Constructor from super class
	 * @param host host string to connect to
	 * @param port port on host to connect to
	 */
	public GameHandler(String host, int port) {
		super(host, port);
	}

	@Override
	public void handlePacket(byte[] raw) {
		if(raw.length <= 2)
		{
			log.warning("Packet with no data ?? size:" + raw[0]+":"+raw[1]);
			return;
		}
		short cde = (short) (raw[2]  & 0xff); //ByteUtils.Sbyte2int(raw[2]);
		short ex = 0;
		if((cde & 0xFF) == 0xfe && raw.length>=4)
			ex = (short)((raw[4] << 8) | (raw[3] & 0xff));//ByteUtils.Sbyte2int(raw[3]) + ByteUtils.Sbyte2int(raw[4]) * 256;
		GameServerPacket pa = null;
		switch (status) {
		case STATE_INITIAL: {
			switch (cde) {
			case 0x20:
				pa = new ServerClose();
				break;
			case 0x2e:
				pa = new KeyPacket();
				status = STATE_AUTHENTIFIED;
				break;
			}
			break;
		}
		case STATE_AUTHENTIFIED: {
			switch (cde) {
			case 0x09:
				pa = new CharSelectionInfo();
				break;
			case 0x20:
				pa = new ServerClose();
				break;
			}
		}
		case STATE_INGAME: {
			switch (cde & 0xFF) {
			case 0x00:
				pa = new Die();
				break;
			case 0x01:
				pa = new Revive();
				break;
			case 0x05:
				pa = new SpawnItem();
				break;
			case 0x06:
//				pa = new SellList();
				break;
			case 0x07:
//				pa = new BuyList();
				break;
			case 0x08:
				pa = new DeleteObject();
				break;
			case 0x0b:
				pa = new CharSelected();
				break;
			case 0x0c:
				pa = new NpcInfo();
				break;
			case 0x0d:
//				pa = new NewCharacterSuccess();
				break;
			case 0x0f:
				pa = new CharCreateOk();
				break;
			case 0x10:
				pa = new CharCreateFail();
				break;
			case 0x11:
				pa = new ItemList();
				break;
			case 0x12:
//				pa = new SunRise();
				break;
			case 0x13:
//				pa = new SunSet();
				break;
			case 0x14:
//				pa = new TradeStart();
				break;
			case 0x16:
				pa = new DropItem();
				break;
			case 0x17:
//				pa = new GetItem();
				break;
			case 0x18:
				pa = new StatusUpdate();
				break;
			case 0x19:
//				pa = new NpcHtmlMessage();
				break;
			case 0x1a:
//				pa = new TradeOwnAdd();
				break;
			case 0x1b:
//				pa = new TradeOtherAdd();
				break;
			case 0x1c:
//				pa = new TradeDone();
				break;
			case 0x1d:
//				pa = new CharDeleteSuccess();
				break;
			case 0x1e:
//				pa = new CharDeleteFail();
				break;
			case 0x1f:
				pa = new ActionFailed();
				break;
			case 0x20:
				pa = new ServerClose();
				break;
			case 0x21:
//				pa = new InventoryUpdate();
				break;
			case 0x22:
				pa = new TeleportToLocation();
				break;
			case 0x23:
				pa = new TargetSelected();
				break;
			case 0x24:
				pa = new TargetUnselected();
				break;
			case 0x25:
				pa = new AutoAttackStart();
				break;
			case 0x26:
				pa = new AutoAttackStop();
				break;
			case 0x27:
				pa = new SocialAction();
				break;
			case 0x28:
				pa = new ChangeMoveType();
				break;
			case 0x29:
//				pa = new ChangeWaitType();
				break;
			case 0x2a:
//				pa = new ManagePledgePower();
				break;
			case 0x2c:
//				pa = new AskJoinPledge();
				break;
			case 0x2f:
				pa = new MoveToLocation();
				break;
			case 0x30:
//				pa = new NpcSay();
				break;
			case 0x31:
				pa = new CharInfo();
				break;
			case 0x32:
				pa = new UserInfo();
				break;
			case 0x33:
				pa = new Attack();
				break;
			case 0x39:
//				pa = new AskJoinParty();
				break;
			case 0x3a:
//				pa = new JoinParty();
				break;
			case 0x41:
//				pa = new WareHouseDepositList();
				break;
			case 0x42:
//				pa = new SortedWareHouseWithdrawalList();
				break;
			case 0x44:
//				pa = new ShortCutRegister();
				break;
			case 0x45:
				pa = new ShortCutInit();
				break;
			case 0x47:
				pa = new StopMove();
				break;
			case 0x48:
//				pa = new MagicSkillUse();
				break;
			case 0x49:
//				pa = new MagicSkillCanceld();
				break;
			case 0x4b:
//				pa = new EquipUpdate();
				break;
			case 0x4c:
//				pa = new DoorInfo();
				break;
			case 0x4d:
//				pa = new DoorStatusUpdate();
				break;
			case 0x4a:
				pa = new CreatureSay();
				break;
			case 0x4e:
//				pa = new PartySmallWindowAll();
				break;
			case 0x4f:
//				pa = new PartySmallWindowAdd();
				break;
			case 0x50:
//				pa = new PartySmallWindowDeleteAll();
				break;
			case 0x51:
//				pa = new PartySmallWindowDelete();
				break;
			case 0x52:
//				pa = new PartySmallWindowUpdate();
				break;
			case 0x54:
//				pa = new MagicSkillLaunched();
				break;
			case 0x5a:
//				pa = new PledgeShowMemberListAll();
				break;
			case 0x5b:
//				pa = new PledgeShowMemberListUpdate();
				break;
			case 0x5c:
//				pa = new PledgeShowMemberListAdd();
				break;
			case 0x5d:
//				pa = new PledgeShowMemberListDelete();
				break;
			case 0x5f:
				pa = new SkillList();
				break;
			case 0x60:
//				pa = new VehicleInfo();
				break;
			case 0x61:
//				pa = new StopRotation();
				break;
			case 0x62:
				pa = new SystemMessage();
				break;
			case 0x63:
//				pa = new StartPledgeWar();
				break;
			case 0x65:
//				pa = new StopPledgeWar();
				break;
			case 0x67:
//				pa = new SurrenderPledgeWar();
				break;
			case 0x6a:
//				pa = new PledgeCrest();
				break;
			case 0x6b:
//				pa = new SetupGauge();
				break;
			case 0x6c:
//				pa = new VehicleDeparture();
				break;
			case 0x6d:
//				pa = new VehicleCheckLocation();
				break;
			case 0x6e:
//				pa = new GetOnVehicle();
				break;
			case 0x6f:
//				pa = new GetOffVehicle();
				break;
			case 0x70:
//				pa = new SendTradeRequest();
				break;
			case 0x71:
//				pa = new RestartResponse();
				break;
			case 0x72:
				pa = new MoveToPawn();
				break;
			case 0x73:
//				pa = new SSQInfo();
				break;
			case 0x74:
//				pa = new GameGuardQuery();
				break;
			case 0x75:
//				pa = new FriendList();
				break;
			case 0x78:
//				pa = new JoinPledge();
				break;
			case 0x79:
				pa = new ValidateLocation();
				break;
			case 0x80:
//				pa = new ValidateLocationInVehicle();
				break;
			case 0x7a:
				pa = new StartRotation();
				break;
			case 0x7b:
//				pa = new ShowBoard();
				break;
			case 0x7c:
//				pa = new ChooseInventoryItem();
				break;
			case 0x7e:
//				pa = new MoveToLocationInVehicle();
				break;
			case 0x7f:
//				pa = new StopMoveInVehicle();
				break;
			case 0x82:
//				pa = new TradeOtherDone();
				break;
			case 0x83:
//				pa = new FriendAddRequest();
				break;
			case 0x84:
//				pa = new LeaveWorld();
				break;
			case 0x85:
//				pa = new AbnormalStatusUpdate();
				break;
			case 0x86:
//				pa = new QuestList();
				break;
			case 0x87:
//				pa = new EnchantResult();
				break;
			case 0x88:
//				pa = new PledgeShowMemberListDeleteAll();
				break;
			case 0x8c:
//				pa = new Ride();
				break;
			case 0x8e:
//				pa = new PledgeShowInfoUpdate();
				break;
			case 0x90:
				pa = new AcquireSkillList();
				break;
			case 0x91:
//				pa = new AcquireSkillInfo();
				break;
			case 0x92:
//				pa = new ServerObjectInfo();
				break;
			case 0x95:
//				pa = new GMViewCharacterInfo();
				break;
			case 0x96:
//				pa = new GMViewPledgeInfo();
				break;
			case 0x97:
//				pa = new GMViewSkillInfo();
				break;
			case 0x99:
//				pa = new GmViewQuestInfo();
				break;
			case 0x9a:
//				pa = new GMViewItemList();
				break;
			case 0x9b:
//				pa = new GMViewWarehouseWithdrawList();
				break;
			case 0x9c:
//				pa = new ListPartyWating();
				break;
			case 0x9d:
//				pa = new PartyMatchDetail();
				break;
			case 0x9e:
//				pa = new PlaySound();
				break;
			case 0x9f:
//				pa = new StaticObject();
				break;
			case 0xa0:
//				pa = new PrivateStoreManageListSell();
				break;
			case 0xa1:
//				pa = new PrivateStoreListSell();
				break;
			case 0xa2:
//				pa = new PrivateStoreMsgSell();
				break;
			case 0xa3:
//				pa = new ShowMiniMap();
				break;
			case 0xa6:
//				pa = new TutorialShowHtml();
				break;
			case 0xa7:
//				pa = new TutorialShowQuestionMark();
				break;
			case 0xa8:
//				pa = new TutorialEnableClientEvent();
				break;
			case 0xa9:
//				pa = new TutorialCloseHtml();
				break;
			case 0xaf:
//				pa = new AllyCrest();
				break;
			case 0xb1:
//				pa = new PetStatusShow();
				break;
			case 0xb2:
//				pa = new PetInfo();
				break;
			case 0xb3:
//				pa = new PetItemList();
				break;
			case 0xb4:
//				pa = new PetInventoryUpdate();
				break;
			case 0xb5:
//				pa = new AllyInfo();
				break;
			case 0xb6:
//				pa = new PetStatusUpdate();
				break;
			case 0xb7:
//				pa = new PetDelete();
				break;
			case 0xb9:
				pa = new MyTargetSelected();
				break;
			case 0xba:
//				pa = new PartyMemberPosition();
				break;
			case 0xbb:
//				pa = new AskJoinAlly();
				break;
			case 0xbd:
//				pa = new PrivateStoreManageListBuy();
				break;
			case 0xbe:
//				pa = new PrivateStoreListBuy();
				break;
			case 0xbf:
//				pa = new PrivateStoreMsgBuy();
				break;
			case 0xc0:
//				pa = new VehicleStarted();
				break;
			case 0xc7:
//				pa = new SkillCoolTime();
				break;
			case 0xc8:
//				pa = new PackageToList();
				break;
			case 0xc9:
//				pa = new FortressSiegeInfo();
				break;
			case 0xca:
//				pa = new FortSiegeAttackerList();
				break;
			case 0xcb:
//				pa = new FortSiegeDefenderList();
				break;
			case 0xcc:
//				pa = new NicknameChanged();
				break;
			case 0xcd:
//				pa = new PledgeStatusChanged();
				break;
			case 0xce:
//				pa = new RelationChanged();
				break;
			case 0xd0:
//				pa = new MultiSellList();
				break;
			case 0xd1:
//				pa = new SetSummonRemainTime();
				break;
			case 0xd2:
//				pa = new PackageSendableList();
				break;
			case 0xd6:
//				pa = new SpecialCamera();
				break;
			case 0xda:
//				pa = new Dice();
				break;
			case 0xd3:
//				pa = new Earthquake();
				break;
			case 0xd4:
//				pa = new FlyToLocation();
				break;
			case 0xd7:
//				pa = new NormalCamera();
				break;
			case 0xdb:
//				pa = new Snoop();
				break;
			case 0xdc:
//				pa = new RecipeBookItemList();
				break;
			case 0xdd:
//				pa = new RecipeItemMakeInfo();
				break;
			case 0xde:
//				pa = new RecipeShopManageList();
				break;
			case 0xdf:
//				pa = new RecipeShopSellList();
				break;
			case 0xe0:
//				pa = new RecipeShopItemInfo();
				break;
			case 0xe1:
//				pa = new RecipeShopMsg();
				break;
			case 0xe2:
//				pa = new ShowCalculator();
				break;
			case 0xe3:
//				pa = new MonRaceInfo();
				break;
			case 0xe4:
//				pa = new HennaItemInfo();
				break;
			case 0xe5:
//				pa = new HennaInfo();
				break;
			case 0xe8:
//				pa = new SendMacroList();
				break;
			case 0xe9:
//				pa = new BuyListSeed();
				break;
			case 0xea:
//				pa = new ShowTownMap();
				break;
			case 0xeb:
//				pa = new ObservationMode();
				break;
			case 0xec:
//				pa = new ObservationReturn();
				break;
			case 0xed:
//				pa = new ChairSit();
				break;
			case 0xee:
//				pa = new HennaEquipList();
				break;
			case 0xef:
//				pa = new SellListProcure();
				break;
			case 0xf0:
//				pa = new GMHennaInfo();
				break;
			case 0xf1:
//				pa = new RadarControl();
				break;
			case 0xf2:
//				pa = new ClientSetTime();
				break;
			case 0xf3:
//				pa = new ConfirmDlg();
				break;
			case 0xf4:
//				pa = new PartySpelled();
				break;
			case 0xf5:
//				pa = new ShopPreviewList();
				break;
			case 0xf7:
//				pa = new CameraMode();
				break;
			case 0xf8:
//				pa = new ShowXMasSeal();
				break;
			case 0xf9:
//				pa = new EtcStatusUpdate();
				break;
			case 0xfa:
//				pa = new ShortBuffStatusUpdate();
				break;
			case 0xfb:
//				pa = new SSQStatus();
				break;
			case 0xfd:
//				pa = new AgitDecoInfo();
				break;
			case 0xfe:
				switch (ex) {
				case 0x01:
//					pa = new ExRegMax();
					break;
				case 0x0e:
//					pa = new ExAutoSoulShot();
					break;
				case 0x12:
//					pa = new ExOpenMPCC();
					break;
				case 0x13:
//					pa = new ExCloseMPCC();
					break;
				case 0x14:
//					pa = new ExShowCastleInfo();
					break;
				case 0x15:
//					pa = new ExShowFortressInfo();
					break;
				case 0x16:
//					pa = new ExShowAgitInfo();
					break;
				case 0x17:
//					pa = new ExShowFortressSiegeInfo();
					break;
				case 0x18:
//					pa = new ExPartyPetWindowAdd();
					break;
				case 0x19:
//					pa = new ExPartyPetWindowUpdate();
					break;
				case 0x1a:
//					pa = new ExAskJoinMPCC();
					break;
				case 0x1b:
//					pa = new ExPledgeCrestLarge();
					break;
				case 0x1e:
//					pa = new ExFishingStart();
					break;
				case 0x1f:
//					pa = new ExFishingEnd();
					break;
				case 0x20:
//					pa = new ExShowQuestInfo();
					break;
				case 0x21:
//					pa = new ExShowQuestMark();
					break;
				case 0x22:
//					pa = new ExSendManorList();
					break;
				case 0x23:
//					pa = new ExShowSeedInfo();
					break;
				case 0x24:
//					pa = new ExShowCropInfo();
					break;
				case 0x25:
//					pa = new ExShowManorDefaultInfo();
					break;
				case 0x26:
//					pa = new ExShowSeedSetting();
					break;
				case 0x27:
//					pa = new ExFishingStartCombat();
					break;
				case 0x28:
//					pa = new ExFishingHpRegen();
					break;
				case 0x29:
//					pa = new ExEnchantSkillList();
					break;
				case 0x2a:
//					pa = new ExEnchantSkillInfo();
					break;
				case 0x2b:
//					pa = new ExShowCropSetting();
					break;
				case 0x2c:
//					pa = new ExShowSellCropList();
					break;
				case 0x2d:
//					pa = new ExOlympiadMatchEnd();
					break;
				case 0x2e:
//					pa = new ExMailArrived();
					break;
				case 0x2f:
//					pa = new ExStorageMaxCount();
					break;
				case 0x31:
//					pa = new ExMultiPartyCommandChannelInfo();
					break;
				case 0x32:
//					pa = new ExPCCafePointInfo();
					break;
				case 0x33:
//					pa = new ExSetCompassZoneCode();
					break;
				case 0x34:
//					pa = new ExGetBossRecord();
					break;
				case 0x38:
//					pa = new ExShowAdventurerGuideBook();
					break;
				case 0x39:
//					pa = new ExShowScreenMessage();
					break;
				case 0x3a:
//					pa = new PledgeSkillList();
					break;
				case 0x3b:
//					pa = new PledgeSkillListAdd();
					break;
				case 0x3c:
//					pa = new PledgePowerGradeList();
					break;
				case 0x3d:
//					pa = new PledgeReceivePowerInfo();
					break;
				case 0x3e:
//					pa = new PledgeReceiveMemberInfo();
					break;
				case 0x3f:
//					pa = new PledgeReceiveWarList();
					break;
				case 0x40:
//					pa = new PledgeReceiveSubPledgeCreated();
					break;
				case 0x41:
//					pa = new ExRedSky();
					break;
				case 0x44:
//					pa = new ShowPCCafeCouponShowUI();
					break;
				case 0x35:
//					pa = new ExAskJoinPartyRoom();
					break;
				case 0x45:
//					pa = new ExSearchOrc();
					break;
				case 0x46:
//					pa = new ExCursedWeaponList();
					break;
				case 0x47:
//					pa = new ExCursedWeaponLocation();
					break;
				case 0x48:
//					pa = new ExRestartClient();
					break;
				case 0x49:
//					pa = new ExRequestHackShield();
					break;
				case 0x4a:
//					pa = new ExUseSharedGroupItem();
					break;
				case 0x4b:
//					pa = new ExMPCCShowPartyMemberInfo();
					break;
				case 0x4c:
//					pa = new ExDuelAskStart();
					break;
				case 0x4d:
//					pa = new ExDuelReady();
					break;
				case 0x4e:
//					pa = new ExDuelStart();
					break;
				case 0x4f:
//					pa = new ExDuelEnd();
					break;
				case 0x50:
//					pa = new ExDuelUpdateUserInfo();
					break;
				case 0x51:
//					pa = new ExShowVariationMakeWindow();
					break;
				case 0x52:
//					pa = new ExShowVariationCancelWindow();
					break;
				case 0x53:
//					pa = new ExPutItemResultForVariationMake();
					break;
				case 0x54:
//					pa = new ExPutIntensiveResultForVariationMake();
					break;
				case 0x55:
//					pa = new ExPutCommissionResultForVariationMake();
					break;
				case 0x56:
//					pa = new ExVariationResult();
					break;
				case 0x57:
//					pa = new ExPutItemResultForVariationCancel();
					break;
				case 0x58:
//					pa = new ExVariationCancelResult();
					break;
				case 0x5c:
//					pa = new ExPlayScene();
					break;
				case 0x5d:
//					pa = new ExSpawnEmitter();
					break;
				case 0x5b:
//					pa = new ExMPCCPartyInfoUpdate();
					break;
				case 0x5e:
//					pa = new ExEnchantSkillInfoDetail();
					break;
				case 0x5f:
//					pa = new ExBasicActionList();
					break;
				case 0x60:
//					pa = new ExAirShipInfo();
					break;
				case 0x61:
//					pa = new ExAttributeEnchantResult();
					break;
				case 0x62:
//					pa = new ExChooseInventoryAttributeItem();
					break;
				case 0x63:
//					pa = new ExGetOnAirShip();
					break;
				case 0x64:
//					pa = new ExGetOffAirShip();
					break;
				case 0x65:
//					pa = new ExMoveToLocationAirShip();
					break;
				case 0x66:
//					pa = new ExAirShipStopMove();//FIXME there are two different packs on 66
					break;
				case 0x67:
//					pa = new ExShowTrace();
					break;
				case 0x6a:
//					pa = new ExPartyPetWindowDelete();
					break;
				case 0x6c:
//					pa = new ExRpItemLink();
					break;
				case 0x6d:
//					pa = new ExMoveToLocationInAirShip();
					break;
				case 0x6e:
//					pa = new ExStopMoveInAirShip();
					break;
				case 0x6f:
//					pa = new ExValidateLocationInAirShip();
					break;
				case 0x74:
//					pa = new ExShowBaseAttributeCancelWindow();
					break;
				case 0x78:
//					pa = new ExShowProcureCropDetail();
					break;
				case 0x79:
//					pa = new ExHeroList();
					break;
				case 0x7a:
//					pa = new ExOlympiadUserInfoSpectator();
					break;
				case 0x7b:
//					pa = new ExOlympiadSpelledInfo();
					break;
				case 0x7c:
//					pa = new ExOlympiadMode();
					break;
				case 0x7d:
//					pa = new ExShowFortressMapInfo();
					break;
				case 0x80:
//					pa = new ExPrivateStoreSetWholeMsg();
					break;
				case 0x81:
//					pa = new ExPutEnchantTargetItemResult();//FIXME REQUESTENCHAT
					break;
				case 0x82:
//					pa = new ExPutEnchantSupportItemResult();//FIXME tradeotherdone
					break;
				case 0x84:
//					pa = new ExGetBookMarkInfoPacket();
					break;
				case 0x8c:
//					pa = new ExBrBroadcastEventState();
					break;
				case 0x8d:
//					pa = new NpcQuestHtmlMessage();
					break;
				case 0x90:
//					pa = new ExShowDominionRegistry();
					break;
				case 0xa0:
//					pa = new ExVitalityPointInfo();
					break;
				case 0xaa:
//					pa = new PremiumState();
					break;
				case 0xda:
					pa = new ExBrExtraUserInfo();
					break;
				default:
					break;//TR just to distinguish new from failed packets

				}
				break;
			case 0xff:
				break;
			default:
				break;//TR just to distinguish new from failed packets

			}
		}
		}
		if (pa == null) {
			if((cde & 0xFF) == 0xfe)
				log.warning("Unknown optcode (hex:): 0xfe." + Integer.toHexString(ex)+":"+ex+ " packetsize:"+raw.length);
			else
				log.warning("Unknown optcode (hex:): " + Integer.toHexString(cde)+":"+cde+ " packetsize:"+raw.length);
				
			return;
		}
		pa.setBytes(raw);
		pa.setClientFacade(clientFacade);
		try{
			pa.handlePacket();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to handle game packet"
					+ pa.getClass().getSimpleName() + " Check packet code!", e);
		}
	}


	@Override
	public void onDisconnect() {
			sendPacket(new Logout());
	}

	@Override
	protected void onConnect() {
		//changed from 851 to 85
		//FIXME move this out to properties should be consistent with server development
		//revision 3978 (l2j 3.7 gracia epilogue) uses 146 protocol version
		//revision 4548 uses 216
		//revision 5135 uses 273

		sendPacket(new ProtocolVersion(273));
	}
}
