
//Title:        CRS Test Project
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David Fairchild
//Company:      XMAX Corp
//Description:  This is a first shot at some CRS classes

package xmax.TranServer;

/**
 * This is a utility class which enumerates the following control
 * values: client operation commands, airware commands,
 * service operation responses, airware responses, and command status values
 *
 * @author       David Fairchild
 * @version      1.x Copyright (c) 1999
 */

public class GnrcConvControl
{
 // These are the allowed client operation commands
 public static final String GET_PNR_CMD          = "CGETPNR ";
 public static final String GET_QUEUE_PNR_CMD    = "CPULPNR ";
 public static final String GET_STATUS_CMD       = "CGETSTS ";
 public static final String GET_HOTEL_INFO_CMD   = "CGETHTL ";
 public static final String GET_FLIGHT_INFO_CMD  = "CGETFLT ";
 public static final String GET_AVAIL_CMD        = "CGETAVL ";
 public static final String FREEFORM_CMD         = "CPSSTHRU";
 public static final String START_LOG_CMD        = "CSTRTLOG";
 public static final String END_LOG_CMD          = "CENDLOG ";
 public static final String GET_FARE_CMD         = "CGETFARE";
 public static final String SET_PRN_CMD          = "CSETPRN ";
 public static final String LIST_BRANCH_CMD      = "CLSTBRCH";
 public static final String LIST_GROUP_PROF_CMD  = "CLSTGRP ";
 public static final String LIST_PER_PROF_CMD    = "CLSTPER ";
 public static final String GET_GROUP_PROF_CMD   = "CGETGPRF";
 public static final String GET_PER_PROF_CMD     = "CGETPPRF";
 public static final String SET_GROUP_PROF_CMD   = "CSETGPRF";
 public static final String SET_PER_PROF_CMD     = "CSETPPRF";
 public static final String GET_CONN_TM_CMD      = "CGETCONN";
 public static final String GET_AIRPORT_TM_CMD   = "CAIRPCON";
 public static final String CHG_NAME_CMD         = "CCHGNAME";
 public static final String REPL_NAME_CMD        = "CRPLNAME";
 public static final String CHG_PNR_ITIN_CMD     = "CCHGITIN";
 public static final String SPLIT_PNR_CMD        = "CSPLTPNR";
 public static final String CXL_SEG_CMD          = "CCXLSEG ";
 public static final String CXL_ITIN_CMD         = "CCXLITIN";
 public static final String ADD_RMK_CMD          = "CADDRMK ";
 public static final String MOD_RMK_CMD          = "CMODRMK ";
 public static final String DEL_RMK_CMD          = "CDELRMK ";
 public static final String ADD_BLK_CMD          = "CADDBLK ";
 public static final String ADD_BLK3_CMD         = "CADDBL3 ";
 public static final String MOD_BLK_CMD          = "CMODBLK ";
 public static final String DEL_BLK_CMD          = "CDELBLK ";
 public static final String GET_BLK_CMD          = "CGETBLK ";
 public static final String READ_BLK_MSG_CMD     = "CNEGOMSG";

/*
 ***********************************************************************
 * Airware Request Commands
 ***********************************************************************
 */

/**
 * the string <code>AIRAVAIL</code>, which corresponds to
 * Airware's <code>RequestAirAvail</code> verb
 * @see {@link xmax.Transerver.NativeAsciiReader.reqGetAirwareAvailInfo}
 * @see {@link xmax.Transerver.ReqGetAvail}
 */
 public static final String AWR_GET_AVAIL_CMD    = "AIRAVAIL";
 public static final String AWR_FLIFO_CMD        = "FLIFO   ";

/*
 * The following verbs are used to build a PNR
 */

 public static final String AWR_ADD_PHONE_CMD    = "ADDFONE ";
 /** Add 'Received-By' remark  */
 public static final String AWR_RCV_PNR_CMD      = "RCVPNR  ";
 /** Add Passenger Names  */
 public static final String AWR_ADD_NAME_CMD     = "ADDNAME ";
 /** Add Air Segments */
 public static final String AWR_ADD_AIRSEG_CMD   = "ENTAIRSG";
 /** Add ticketing instructions */
 public static final String AWR_ADD_TICKET_CMD   = "ADDTKT  ";
 /** Add Form of Payment */
 public static final String AWR_ADD_FOP_CMD      = "ADDFOP  ";
 public static final String AWR_ADD_TOURCODE_CMD = "ADDTR   ";
 public static final String AWR_ADD_ENDORSE_CMD  = "ADDEN   ";
 public static final String AWR_ADD_REMARK_CMD   = "ADDRMK  ";
 /** Add Commission */
 public static final String AWR_ADD_COMM_CMD     = "ADDCOM  ";

 /** Retrieve a PNR */
 public static final String AWR_GET_PNR_CMD      = "GETPNR  ";

 /** temporary command to migrate to a different GET_PNR response */
 public static final String AWR_GET_PNR2_CMD     = "GETPNR2 ";

 public static final String AWR_CHG_NAME_CMD     = "CHGNAME ";
 public static final String AWR_CXL_REMARK_CMD   = "CXLRMK  ";

 public static final String AWR_FARE_CMD         = "FARITIN ";
 public static final String AWR_TKT_PNR_CMD      = "TICKET  ";
 public static final String AWR_TKT_INFO_CMD     = "TKTPNR  ";
 public static final String AWR_CON_TM_CMD       = "GETCON  ";
 public static final String AWR_GRP_HDR_CMD      = "ADDGPHDR";
 public static final String AWR_SCHED_CHG_CMD    = "CHGQUEUE";
 public static final String AWR_QUEUE_PNR_CMD    = "ENQUEUE ";
 public static final String AWR_END_XACT_CMD     = "ENDXACT ";
 public static final String AWR_IGNORE_CMD       = "IGNORE  ";
 public static final String AWR_FREEFORM_CMD     = "FREEFORM";
 public static final String AWR_STARTSES_CMD     = "STARTSES";
 public static final String AWR_ACCEPT_CHG_CMD   = "CHGQUEUE";
 public static final String AWR_ENDSES_CMD       = "ENDSES  ";


 // These are the allowed service operation responses
 public static final String ERROR_RESP           = "RGETERR ";
 public static final String GET_PNR_RESP         = "RGETPNR ";
 public static final String GET_QUEUE_PNR_RESP   = "RPULPNR ";
 public static final String GET_STATUS_RESP      = "RGETSTS ";
 public static final String GET_HOTEL_INFO_RESP  = "RGETHTL ";
 public static final String GET_FLIGHT_INFO_RESP = "RGETFLT ";
 public static final String GET_AVAIL1_RESP      = "RGETAV1 ";
 public static final String GET_AVAIL2_RESP      = "RGETAV2 ";
 public static final String GET_AVAIL3_RESP      = "RGETAV3 ";
 public static final String FREEFORM_RESP        = "RPSSTHRU";
 public static final String START_LOG_RESP       = "RSTRTLOG";
 public static final String END_LOG_RESP         = "RENDLOG ";
 public static final String GET_FARE_RESP        = "RGETFARE";
 public static final String SET_PRN_RESP         = "RSETPRN ";

 // airware responses
 public static final String AWR_GET_PNR_RESP      = "GETPNR  ";

 public static final String AWR_ADD_NAME_RESP     = "ADDNAME ";
 public static final String AWR_CHG_NAME_RESP     = "CHGNAME ";
 public static final String AWR_ADD_PHONE_RESP    = "ADDFONE ";
 public static final String AWR_ADD_TICKET_RESP   = "ADDTKT  ";
 public static final String AWR_ADD_AIRSEG_RESP   = "ENTAIRSG";
 public static final String AWR_ADD_FOP_RESP      = "ADDFOP  ";
 public static final String AWR_ADD_TOURCODE_RESP = "ADDTR   ";
 public static final String AWR_ADD_ENDORSE_RESP  = "ADDEN   ";
 public static final String AWR_ADD_REMARK_RESP   = "ADDRMK  ";
 public static final String AWR_CXL_REMARK_RESP   = "CXLRMK  ";
 public static final String AWR_ADD_COMM_RESP     = "ADDCOM  ";
 public static final String AWR_RCV_PNR_RESP      = "RCVPNR  ";
 public static final String AWR_FLIFO_RESP        = "FLIFO   ";
// public static final String AWR_FARE_RESP         = "FARITIN ";
 public static final String AWR_TKT_PNR_RESP      = "TICKET  ";
 public static final String AWR_CON_TM_RESP       = "GETCON  ";
 public static final String AWR_GRP_HDR_RESP      = "ADDGPHDR";
 public static final String AWR_SCHED_CHG_RESP    = "CHGQUEUE";
 public static final String AWR_QUEUE_PNR_RESP    = "ENQUEUE ";
 public static final String AWR_END_XACT_RESP     = "ENDXACT ";
 public static final String AWR_IGNORE_RESP       = "IGNORE  ";
 public static final String AWR_FREEFORM_RESP     = "FREEFORM";
 public static final String AWR_AVL_ITIN_RESP     = "AIRAVAI1";
 public static final String AWR_AVL_CLASS_RESP    = "AIRAVAI2";
 public static final String AWR_STARTSES_RESP     = "STARTSES";
 public static final String AWR_ACCEPT_CHG_RESP   = "CHGQUEUE";
 public static final String AWR_ENDSES_RESP       = "ENDSES  ";
 public static final String LIST_BRANCH_RESP      = "RLSTBRCH";
 public static final String LIST_GROUP_PROF_RESP  = "RLSTGRP ";
 public static final String LIST_PER_PROF_RESP    = "RLSTPER ";
 public static final String GET_GROUP_PROF_RESP   = "RGETGPRF";
 public static final String GET_PER_PROF_RESP     = "RGETPPRF";
 public static final String SET_GROUP_PROF_RESP   = "RSETGPRF";
 public static final String SET_PER_PROF_RESP     = "RSETPPRF";
 public static final String GET_CONN_TM_RESP      = "RGETCONN";
 public static final String GET_AIRPORT_TM_RESP   = "RAIRPCON";
 public static final String CHG_NAME_RESP         = "RCHGNAME";
 public static final String CHG_PNR_ITIN_RESP     = "RCHGITIN";
 public static final String SPLIT_PNR_RESP        = "RSPLTPNR";
 public static final String CXL_SEG_RESP          = "RCXLSEG ";
 public static final String CXL_ITIN_RESP         = "RCXLITIN";
 public static final String ADD_RMK_RESP          = "RADDRMK ";
 public static final String MOD_RMK_RESP          = "RMODRMK ";
 public static final String DEL_RMK_RESP          = "RDELRMK ";
 public static final String ADD_BLK_RESP          = "RADDBLK ";
 public static final String ADD_BLK3_RESP         = "RADDBL3 ";
 public static final String MOD_BLK_RESP          = "RMODBLK ";
 public static final String DEL_BLK_RESP          = "RDELBLK ";
 public static final String GET_BLK_RESP          = "RGETBLK ";
 public static final String READ_BLK_MSG_RESP     = "RNEGOMSG";

 // command status values
 public static final int STATUS_OK        = 50001;            // command was run OK
 public static final int STS_NO_TA        = 59001;            // no TAs are available
 public static final int STS_NO_HOST      = 59002;            // host is not responding
 public static final int STS_INVLD_REQ    = 59003;            // invalid request
 public static final int STS_NO_ITINS     = 59004;            // no itineraries
 public static final int STS_NOT_FOUND    = 59005;            // not found
 public static final int STS_TIMESTAMP    = 59006;            // unable to update since GDS timestamp was later
 public static final int STS_NO_AVAIL     = 59038;            // no availabilities
 public static final int STS_BAD_DATA     = 59996;            // bad CRS data
 public static final int STS_CRS_ERR      = 59997;            // CRS error
 public static final int STS_REQ_DATA_ERR = 59998;            // requested data errorr
 public static final int STS_CRS_UNAVAIL  = 59999;            // CRS temporarily unavailable
 public static final int STS_NO_FEEDERS   = 33100;            // no feeders
 public static final int STS_CRS_BAD      = 39001;            // CRS down
 public static final int STS_NO_SELL      = 59303;            // unable to sell air segment
 public static final int STS_NO_REMARKS   = 59304;            // remark error
 public static final int STS_NR_SEATS     = 59305;            // number of seats error
 public static final int STS_PSGR_ERR     = 59306;            // passenger error
 public static final int STS_BKF_NOT_RDY  = 59307;            // not ready to book feeders
 public static final int STS_FNL_NOT_RDY  = 59311;            // not ready for final
 public static final int STS_ERR_GET_PNR  = 59400;            // error getting PNR
 public static final int STS_NO_QUEUE     = 59500;            // queue undefined
 public static final int STS_QUEUE_ERR    = 59601;            // queue error
 public static final int STS_DEV_NO_DO    = 59701;            // ???
 public static final int STS_FORMAT_ERR   = 59702;            // Transaction Server formatting error
 public static final int STS_1A_DLL_ERR   = 58000;            // base error code for Amadeus API dll errs
 public static final int STS_QUEUE_EMPTY  = 59969;            // base error code for Amadeus API dll errs

}
