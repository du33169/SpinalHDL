package spinal.lib.memory.sdram.Dfi.CtrlWithBmb

import spinal.lib._
import spinal.core._
import spinal.lib.memory.sdram.Dfi.Interface.{TaskParameterAggregate, IDFI, TaskPort}
import spinal.lib.memory.sdram.Dfi._

case class Control(tpa : TaskParameterAggregate) extends Component{
  import tpa._
  val io = new Bundle{
    val inport  = slave(TaskPort(tpp, tpa))
    val outport = master(IDFI(config))
  }

  val cmd = new Area {
    val cmdtxd = CmdTxd(tpa)
    cmdtxd.io.task <> io.inport.tasks.init()
  }

  val wrdata = new Area {
    val wrdatatxd = WrDataTxd(tpa)
    wrdatatxd.io.write <> RegNext(io.inport.tasks.task.write).init(False)
    wrdatatxd.io.coreWrdata << io.inport.writeData
  }

  val radata = new Area {
    val rddatarxd = RdDataRxd(tpa)
    rddatarxd.io.task <> io.inport.tasks.init()
    rddatarxd.io.coreRddata.toStream >> io.inport.rsp
  }

  val idfiout = new Area {
    io.outport.cmd <> cmd.cmdtxd.io.cmd
    io.outport.address <> cmd.cmdtxd.io.address
    io.outport.wrdata <> wrdata.wrdatatxd.io.idfiWrdata
    io.outport.rddata <> radata.rddatarxd.io.idfiRddata
    io.outport.rden <> radata.rddatarxd.io.rden
  }


}
