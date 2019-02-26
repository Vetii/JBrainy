package se.lth.cs

import papi.Constants
import java.io.File
import java.io.FileReader
import java.io.Reader

class CounterSpecification(strings : List<String>) {

    val currentSpec = allCounters.filterKeys { strings.contains(it) }

    fun getCounter(name : String): Int? {
        return currentSpec[name]
    }

    companion object {
        /**
         * All the hardware performance counters
         */
        val allCounters =
                sortedMapOf(
                        "PAPI_L1_DCM" to Constants.PAPI_L1_DCM,
                        "PAPI_L1_ICM" to Constants.PAPI_L1_ICM,
                        "PAPI_L2_DCM" to Constants.PAPI_L2_DCM,
                        "PAPI_L2_ICM" to Constants.PAPI_L2_ICM,
                        "PAPI_L3_DCM" to Constants.PAPI_L3_DCM,
                        "PAPI_L3_ICM" to Constants.PAPI_L3_ICM,
                        "PAPI_L1_TCM" to Constants.PAPI_L1_TCM,
                        "PAPI_L2_TCM" to Constants.PAPI_L2_TCM,
                        "PAPI_L3_TCM" to Constants.PAPI_L3_TCM,
                        "PAPI_CA_SNP" to Constants.PAPI_CA_SNP,
                        "PAPI_CA_SHR" to Constants.PAPI_CA_SHR,
                        "PAPI_CA_CLN" to Constants.PAPI_CA_CLN,
                        "PAPI_CA_INV" to Constants.PAPI_CA_INV,
                        "PAPI_CA_ITV" to Constants.PAPI_CA_ITV,
                        "PAPI_L3_LDM" to Constants.PAPI_L3_LDM,
                        "PAPI_L3_STM" to Constants.PAPI_L3_STM,
                        "PAPI_BRU_IDL" to Constants.PAPI_BRU_IDL,
                        "PAPI_FXU_IDL" to Constants.PAPI_FXU_IDL,
                        "PAPI_FPU_IDL" to Constants.PAPI_FPU_IDL,
                        "PAPI_LSU_IDL" to Constants.PAPI_LSU_IDL,
                        "PAPI_TLB_DM" to Constants.PAPI_TLB_DM,
                        "PAPI_TLB_IM" to Constants.PAPI_TLB_IM,
                        "PAPI_TLB_TL" to Constants.PAPI_TLB_TL,
                        "PAPI_L1_LDM" to Constants.PAPI_L1_LDM,
                        "PAPI_L1_STM" to Constants.PAPI_L1_STM,
                        "PAPI_L2_LDM" to Constants.PAPI_L2_LDM,
                        "PAPI_L2_STM" to Constants.PAPI_L2_STM,
                        "PAPI_BTAC_M" to Constants.PAPI_BTAC_M,
                        "PAPI_PRF_DM" to Constants.PAPI_PRF_DM,
                        "PAPI_L3_DCH" to Constants.PAPI_L3_DCH,
                        "PAPI_TLB_SD" to Constants.PAPI_TLB_SD,
                        "PAPI_CSR_FAL" to Constants.PAPI_CSR_FAL,
                        "PAPI_CSR_SUC" to Constants.PAPI_CSR_SUC,
                        "PAPI_CSR_TOT" to Constants.PAPI_CSR_TOT,
                        "PAPI_MEM_SCY" to Constants.PAPI_MEM_SCY,
                        "PAPI_MEM_RCY" to Constants.PAPI_MEM_RCY,
                        "PAPI_MEM_WCY" to Constants.PAPI_MEM_WCY,
                        "PAPI_STL_ICY" to Constants.PAPI_STL_ICY,
                        "PAPI_FUL_ICY" to Constants.PAPI_FUL_ICY,
                        "PAPI_STL_CCY" to Constants.PAPI_STL_CCY,
                        "PAPI_FUL_CCY" to Constants.PAPI_FUL_CCY,
                        "PAPI_HW_INT" to Constants.PAPI_HW_INT,
                        "PAPI_BR_UCN" to Constants.PAPI_BR_UCN,
                        "PAPI_BR_CN" to Constants.PAPI_BR_CN,
                        "PAPI_BR_TKN" to Constants.PAPI_BR_TKN,
                        "PAPI_BR_NTK" to Constants.PAPI_BR_NTK,
                        "PAPI_BR_MSP" to Constants.PAPI_BR_MSP,
                        "PAPI_BR_PRC" to Constants.PAPI_BR_PRC,
                        "PAPI_FMA_INS" to Constants.PAPI_FMA_INS,
                        "PAPI_TOT_IIS" to Constants.PAPI_TOT_IIS,
                        "PAPI_TOT_INS" to Constants.PAPI_TOT_INS,
                        "PAPI_INT_INS" to Constants.PAPI_INT_INS,
                        "PAPI_FP_INS" to Constants.PAPI_FP_INS,
                        "PAPI_LD_INS" to Constants.PAPI_LD_INS,
                        "PAPI_SR_INS" to Constants.PAPI_SR_INS,
                        "PAPI_BR_INS" to Constants.PAPI_BR_INS,
                        "PAPI_VEC_INS" to Constants.PAPI_VEC_INS,
                        "PAPI_RES_STL" to Constants.PAPI_RES_STL,
                        "PAPI_FP_STAL" to Constants.PAPI_FP_STAL,
                        "PAPI_TOT_CYC" to Constants.PAPI_TOT_CYC,
                        "PAPI_LST_INS" to Constants.PAPI_LST_INS,
                        "PAPI_SYC_INS" to Constants.PAPI_SYC_INS,
                        "PAPI_L1_DCH" to Constants.PAPI_L1_DCH,
                        "PAPI_L2_DCH" to Constants.PAPI_L2_DCH,
                        "PAPI_L1_DCA" to Constants.PAPI_L1_DCA,
                        "PAPI_L2_DCA" to Constants.PAPI_L2_DCA,
                        "PAPI_L3_DCA" to Constants.PAPI_L3_DCA,
                        "PAPI_L1_DCR" to Constants.PAPI_L1_DCR,
                        "PAPI_L2_DCR" to Constants.PAPI_L2_DCR,
                        "PAPI_L3_DCR" to Constants.PAPI_L3_DCR,
                        "PAPI_L1_DCW" to Constants.PAPI_L1_DCW,
                        "PAPI_L2_DCW" to Constants.PAPI_L2_DCW,
                        "PAPI_L3_DCW" to Constants.PAPI_L3_DCW,
                        "PAPI_L1_ICH" to Constants.PAPI_L1_ICH,
                        "PAPI_L2_ICH" to Constants.PAPI_L2_ICH,
                        "PAPI_L3_ICH" to Constants.PAPI_L3_ICH,
                        "PAPI_L1_ICA" to Constants.PAPI_L1_ICA,
                        "PAPI_L2_ICA" to Constants.PAPI_L2_ICA,
                        "PAPI_L3_ICA" to Constants.PAPI_L3_ICA,
                        "PAPI_L1_ICR" to Constants.PAPI_L1_ICR,
                        "PAPI_L2_ICR" to Constants.PAPI_L2_ICR,
                        "PAPI_L3_ICR" to Constants.PAPI_L3_ICR,
                        "PAPI_L1_ICW" to Constants.PAPI_L1_ICW,
                        "PAPI_L2_ICW" to Constants.PAPI_L2_ICW,
                        "PAPI_L3_ICW" to Constants.PAPI_L3_ICW,
                        "PAPI_L1_TCH" to Constants.PAPI_L1_TCH,
                        "PAPI_L2_TCH" to Constants.PAPI_L2_TCH,
                        "PAPI_L3_TCH" to Constants.PAPI_L3_TCH,
                        "PAPI_L1_TCA" to Constants.PAPI_L1_TCA,
                        "PAPI_L2_TCA" to Constants.PAPI_L2_TCA,
                        "PAPI_L3_TCA" to Constants.PAPI_L3_TCA,
                        "PAPI_L1_TCR" to Constants.PAPI_L1_TCR,
                        "PAPI_L2_TCR" to Constants.PAPI_L2_TCR,
                        "PAPI_L3_TCR" to Constants.PAPI_L3_TCR,
                        "PAPI_L1_TCW" to Constants.PAPI_L1_TCW,
                        "PAPI_L2_TCW" to Constants.PAPI_L2_TCW,
                        "PAPI_L3_TCW" to Constants.PAPI_L3_TCW,
                        "PAPI_FML_INS" to Constants.PAPI_FML_INS,
                        "PAPI_FAD_INS" to Constants.PAPI_FAD_INS,
                        "PAPI_FDV_INS" to Constants.PAPI_FDV_INS,
                        "PAPI_FSQ_INS" to Constants.PAPI_FSQ_INS,
                        "PAPI_FNV_INS" to Constants.PAPI_FNV_INS,
                        "PAPI_FP_OPS" to Constants.PAPI_FP_OPS,
                        "PAPI_SP_OPS" to Constants.PAPI_SP_OPS,
                        "PAPI_DP_OPS" to Constants.PAPI_DP_OPS,
                        "PAPI_VEC_SP" to Constants.PAPI_VEC_SP,
                        "PAPI_VEC_DP" to Constants.PAPI_VEC_DP,
                        "PAPI_REF_CYC" to Constants.PAPI_REF_CYC
                )

        /**
         * Loads data from a reader to a list of counter IDs
         * @param reader The reaser from which the data is read (one field per line)
         * @return the list of selected counters
         */
        fun fromReader(reader : Reader) : CounterSpecification {
            return CounterSpecification(reader.readLines())
        }

        /**
         * Loads data from a given file
         * @param file the file to read the counter names from
         * @return the list of selected counters
         */
        fun fromFile(file : File) : CounterSpecification {
            return Companion.fromReader(FileReader(file))
        }
    }
}