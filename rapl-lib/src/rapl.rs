use thiserror::Error;

// Use the OS specific implementation
#[cfg(target_os = "linux")]
pub mod linux;
#[cfg(target_os = "windows")]
pub mod windowss;

// Import the MSR constants per CPU type
#[cfg(amd)]
use crate::rapl::amd::{MSR_RAPL_PKG_ENERGY_STAT, MSR_RAPL_POWER_UNIT};
#[cfg(intel)]
use crate::rapl::intel::{MSR_RAPL_PKG_ENERGY_STAT, MSR_RAPL_POWER_UNIT};

// Import the OS specific MSR read function
#[cfg(target_os = "linux")]
use self::linux::read_msr;
#[cfg(target_os = "windows")]
use self::windowss::read_msr;

#[derive(Error, Debug)]
pub enum RaplError {
    #[cfg(target_os = "windows")]
    #[error("windows error")]
    Windows(#[from] windows::core::Error),
    #[error("unknown RAPL error")]
    Unknown,
}

// Get the CPU type based on the compile time configuration
pub fn get_cpu_type() -> &'static str {
    #[cfg(intel)]
    {
        "Intel"
    }

    #[cfg(amd)]
    {
        "AMD"
    }
}

pub fn read_rapl_power_unit() -> Result<u64, RaplError> {
    read_msr(MSR_RAPL_POWER_UNIT)
}

pub fn read_rapl_pkg_energy_stat() -> Result<u64, RaplError> {
    read_msr(MSR_RAPL_PKG_ENERGY_STAT)
}

#[cfg(amd)]
pub mod amd {
    /*
    https://lore.kernel.org/lkml/20180817163442.10065-2-calvin.walton@kepstin.ca/

    "A notable difference from the Intel implementation is that AMD reports
    the "Cores" energy usage separately for each core, rather than a
    per-package total"
     */
    pub const MSR_RAPL_POWER_UNIT: u64 = 0xC0010299; // Similar to Intel MSR_RAPL_POWER_UNIT
    pub const MSR_RAPL_PKG_ENERGY_STAT: u64 = 0xC001029B; // Similar to Intel PKG_ENERGY_STATUS (This is for the whole socket)
    
    pub const AMD_MSR_CORE_ENERGY: u64 = 0xC001029A; // Similar to Intel PP0_ENERGY_STATUS (PP1 is for the GPU)

    /*
    const AMD_TIME_UNIT_MASK: u64 = 0xF0000;
    const AMD_ENERGY_UNIT_MASK: u64 = 0x1F00;
    const AMD_POWER_UNIT_MASK: u64 = 0xF;
    */
}

#[cfg(intel)]
pub mod intel {
    pub const MSR_RAPL_POWER_UNIT: u64 = 0x606;
    pub const MSR_RAPL_PKG_ENERGY_STAT: u64 = 0x611;
    /*
    const INTEL_MSR_RAPL_PP0: u64 = 0x639;
    const INTEL_MSR_RAPL_PP1: u64 = 0x641;
    const INTEL_MSR_RAPL_DRAM: u64 = 0x619;

    const INTEL_TIME_UNIT_MASK: u64 = 0xF000;
    const INTEL_ENGERY_UNIT_MASK: u64 = 0x1F00;
    const INTEL_POWER_UNIT_MASK: u64 = 0x0F;

    const INTEL_TIME_UNIT_OFFSET: u64 = 0x10;
    const INTEL_ENGERY_UNIT_OFFSET: u64 = 0x08;
    const INTEL_POWER_UNIT_OFFSET: u64 = 0;
    */
}
