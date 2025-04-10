<template>
	<div class="app-container app-container-100" >
		
		<div>
			<el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
				<el-form-item label="客户名称" prop="customerName">
					<el-input v-model="queryParams.customerName" placeholder="请输入客户名称" clearable style="width: 160px"
						@keyup.enter="handleQuery" />
				</el-form-item>
				<el-form-item label="推进人员" prop="userName">
					<el-input v-model="queryParams.userName" placeholder="请输入推进人员姓名" clearable style="width: 160px"
						@keyup.enter="handleQuery" />
				</el-form-item>
		
				<el-form-item label="推进时间">
					<el-date-picker
						v-model="queryParams.timeArr" type="daterange" range-separator="-" start-placeholder="开始时间"	
						end-placeholder="结束时间" style="width: 270px;" value-format="YYYY-MM-DD"></el-date-picker>
				</el-form-item>
		
				<el-form-item label="是否有意向" prop="userName">
					<el-select v-model="queryParams.intention" @change="resetQuery" style="width: 150px;">
						<el-option v-for="(item,index) in intentionList" :key="index" :label="item.name" :value="item.id"></el-option>
					</el-select>
				</el-form-item>
		
				<el-form-item>
					<el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
					<el-button icon="Refresh" @click="resetQuery">重置</el-button>
				</el-form-item>
			</el-form>
		
			<el-row :gutter="10" class="mb8">
			
				<el-col :span="1.5">
					<el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDeleteAll"
						v-hasPermi="DEL_CODE">删除</el-button>
				</el-col>
				<el-col :span="1.5">
					<el-button type="warning" plain icon="Download" @click="handleExport"
					>导出</el-button>
				</el-col>
				<right-toolbar v-model:showSearch="showSearch" @queryTable="getTableData"></right-toolbar>
			</el-row>
			
		</div>
		
		
		
		<div class="self-app-con marketing-app-con">
			<el-table v-loading="loading" :data="tableData" @selection-change="handleSelectionChange" :border="true"
				:stripe="true"  :element-loading-svg="svg" :element-loading-svg-view-box="svgView" element-loading-text="数据加载中..."
				height="100%">
			
				<el-table-column type="selection" width="55" align="center"></el-table-column>
			
				<el-table-column label="跟进人员" prop="userName" align="center" width="110"></el-table-column>
				
				<el-table-column label="拜访人员" prop="contactName" align="center" width="110"></el-table-column>
				
				<el-table-column label="客户名称" prop="customerName" align="left" min-width="120" :show-overflow-tooltip="true"></el-table-column>
				
				<el-table-column label="是否有意向" prop="address" align="center" width="120">
					<template #default="scope">
						<el-tag type="success" v-if="scope.row.intention == 1">有意向</el-tag>
						<el-tag type="danger" v-else>无意向</el-tag>
					</template>
				</el-table-column>
				
				<el-table-column label="意向商品" prop="productName" align="center" width="120">
					<template #default="scope">
						<div v-if="scope.row.productNum > 0" style="cursor: pointer;">
							<el-tooltip effect="dark" :content="scope.row.productText"	placement="top">
								<el-tag type="success">{{ scope.row.productNum }}</el-tag>
							</el-tooltip>
						</div>
		
						<el-tag type="danger" v-else>无</el-tag>
					</template>
				</el-table-column>
				
				<el-table-column label="跟进情况" prop="situation" align="left" min-width="120" :show-overflow-tooltip="true"></el-table-column>
				
				<el-table-column label="备注" prop="remark" align="left" min-width="120" :show-overflow-tooltip="true"></el-table-column>
				
				<el-table-column label="跟进时间" prop="fieldDate" align="left" width="170" ></el-table-column>
				
				<el-table-column label="操作" width="170" align="center" class-name="small-padding fixed-width">
					<template #default="scope">
						<el-button link type="primary" icon="View" @click="handleAddEdit('view',scope.row)"
						>查看</el-button>
							
						<span v-hasPermi="DEL_CODE">
							<el-popconfirm title="是否删除当前商家信息?" @confirm="handleDelete(scope.row.fieldId)">
								<template #reference>
									<el-button link type="danger" icon="Delete">删除</el-button>
								</template>
							</el-popconfirm>
						</span>
						
					</template>
				</el-table-column>
			</el-table>
		</div>
		
		<div class="self-app-page">
			<pagination :total="total" v-model:page="queryParams.pageNum"
			v-model:limit="queryParams.pageSize" @pagination="getTableData" />
		</div>
		
		<add-edit ref="addEditRef" @success="saveSuccess"></add-edit>
	</div>
</template>

<script setup name="MarketingList">
	// import {calcTableHeight} from "@/utils/publicType.js"
	// import marketingApi from "@/api/marketing/list"
	// import addEdit from "./addEdit.vue"
import { onMounted } from "vue";
	const {proxy} = getCurrentInstance()
	const tableData = ref([])
	
	// 加载loading配置
	let loadingConfig = proxy.getSvgConfig()
	const svg = loadingConfig.loadingSvg
	const svgView =  loadingConfig.viewBox
	
	const open = ref(false)
	const loading = ref(false)
	const showSearch = ref(true)
	const ids = ref([])
	const multiple = ref(true)
	const total = ref(0)
	const addEditRef = ref()
	
	const EDIT_CODE = ['erp:marketing:edit']
	const DEL_CODE = ['erp:marketing:DELETE']

	const queryParams = ref({
		pageNum: 1,pageSize: 20,customerName: "",userName:"",
		intention:"",timeArr:[],startDate:"",endDate:"",
	});

	const intentionList = ref([
		{id:"",name:"全部"},
		{id:0,name:"无意向"},
		{id:1,name:"有意向"}
	])


	/** 查询 */
	function getTableData() {
		loading.value = true;
		let formData = new FormData()
		for(let kes in queryParams.value){
			formData.append(kes,queryParams.value[kes])
		}
		formData.delete("timeArr")
		marketingApi.list(formData).then(res => {
			loading.value = false
			if(res.code == 200){
				res.rows.forEach((item)=>{
					if(item.productList && item.productList.length > 0){
						item.productNum = item.productList.length
						let productText = ""
						item.productList.forEach((it)=>{
							productText = productText + it.name + "  ， "
						})
						productText = productText.substring(0,productText.length - 3)
						item.productText = productText
					}else{
						item.productNum = 0
						item.productText = ""
					}
					if(item.contact){
						item.contactName = item.contact.contactName
					}else{
						item.contactName = ""
					}
				})
				tableData.value = res.rows
				total.value = res.total
			}else{
				proxy.$message({type:"warning",message:res.msg})
			}
		}).catch((err)=>{

		})
	}
	
	/** 搜索按钮操作 */
	function handleQuery() {
		let timeArr = queryParams.value.timeArr
		if(timeArr && timeArr.length > 0){
			queryParams.value.startDate = timeArr[0]
			queryParams.value.endDate = timeArr[1]
		}else{
			queryParams.value.startDate = ""
			queryParams.value.endDate = ""
		}
		queryParams.value.pageNum = 1;
		getTableData();
	}
	
	/** 重置按钮操作 */
	function resetQuery() {
		queryParams.value.timeArr = []
		queryParams.value.startDate = ""
		queryParams.value.endDate = ""
		proxy.resetForm("queryRef");
		handleQuery();
	}
	/** 多选框选中数据 */
	function handleSelectionChange(selection) {
		ids.value = selection.map(item => item.fieldId);
		multiple.value = !selection.length;
	}
	
	/** 新增与修改操作 */
	function handleAddEdit(type,data = {}){
		addEditRef.value.showDialog(type,data)
	}
	
	/*添加与保存成功*/
	function saveSuccess(data){
		console.log(data)
	}
	

	/* 删除多个的时候 */
	function handleDeleteAll() {
		if(ids.value.length == 0){
			return false
		}
		proxy.$confirm("是否删除当前选中的跟进记录？",'提示',{type:"error"}).then(()=>{
			let idString = ids.value.join(",")
			handleDelete(idString)
		}).catch(()=>{
			
		})
		
	}

	/** 删除按钮操作 */
	function handleDelete(ids) {
		loading.value = true
		marketingApi.del(ids).then((res)=>{
			loading.value = false
			if(res.code == 200){
				proxy.$message({type:"success",message:"删除成功"})
				getTableData()
			}else{
				proxy.$message({type:"waring",message:res.msg})
			}
		}).catch((err)=>{
			loading.value = false
		})
	}

	/** 导出按钮操作 */
	function handleExport() {
		// proxy.download("system/post/export", {
		//   ...queryParams.value
		// }, `post_${new Date().getTime()}.xlsx`);
	}
	
	onMounted(()=>{
		getTableData()
	})
</script>

<style>
	.marketing-app-con{
		flex: 0;
		max-height: none;
		height: calc(100% - 135px);
	}
	
	@media only screen and (max-width: 1579px) {
	  /* 页面宽度小于等于767px的样式 */
	  .openSidebar .marketing-app-con{
		height: calc(100% - 180px);
	  }
	}
	
	@media only screen and (max-width: 1432px) {
	  /* 页面宽度小于等于767px的样式 */
	  .hideSidebar .marketing-app-con{
		height: calc(100% - 180px);
	  }
	}
</style>