package com.xmbest.floatmaster.model

/**
 * 权限管理系统使用示例
 * 
 * 这个文件展示了如何使用新的通用权限管理系统，以及如何添加新权限
 */

// ========== 如何添加新权限 ==========

/**
 * 1. 在 Permission 枚举中添加新权限
 * 
 * 例如，添加相机权限：
 * 
 * CAMERA(
 *     permissionName = Manifest.permission.CAMERA,
 *     displayNameResId = R.string.camera_permission
 * ),
 * 
 * 添加特殊权限（如通知权限）：
 * 
 * NOTIFICATION(
 *     permissionName = "android.permission.POST_NOTIFICATIONS",
 *     displayNameResId = R.string.notification_permission,
 *     isSpecialPermission = true
 * )
 */

/**
 * 2. 在 strings.xml 中添加对应的显示名称
 * 
 * <string name="camera_permission">相机权限</string>
 * <string name="notification_permission">通知权限</string>
 */

/**
 * 3. 在 Permission 枚举的 isGranted 方法中添加权限检查逻辑
 * 
 * CAMERA -> {
 *     ContextCompat.checkSelfPermission(
 *         context,
 *         permissionName
 *     ) == PackageManager.PERMISSION_GRANTED
 * }
 * NOTIFICATION -> {
 *     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
 *         ContextCompat.checkSelfPermission(
 *             context,
 *             permissionName
 *         ) == PackageManager.PERMISSION_GRANTED
 *     } else {
 *         true
 *     }
 * }
 */

/**
 * 4. 在 PermissionManager 的 requestPermission 方法中添加申请逻辑
 * 
 * Permission.CAMERA -> {
 *     normalPermissionLauncher.launch(permission.permissionName)
 * }
 * Permission.NOTIFICATION -> {
 *     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
 *         normalPermissionLauncher.launch(permission.permissionName)
 *     }
 * }
 */

// ========== 使用示例 ==========

/**
 * 在 Activity 中使用权限管理器：
 * 
 * class MyActivity : ComponentActivity() {
 *     private lateinit var permissionManager: PermissionManager
 * 
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         
 *         permissionManager = PermissionManager(this)
 *         
 *         // 检查所有权限
 *         val permissions = permissionManager.checkAllPermissions()
 *         
 *         // 申请缺失的权限
 *         permissionManager.requestPermissionsIfNeeded(
 *             normalPermissionLauncher,
 *             overlayPermissionLauncher
 *         )
 *         
 *         // 检查特定权限
 *         if (Permission.RECORD_AUDIO.isGranted(this)) {
 *             // 权限已授予，执行相关操作
 *         }
 *     }
 * }
 */

/**
 * 在 ViewModel 中使用：
 * 
 * class MyViewModel : ViewModel() {
 *     fun checkSpecificPermission(permission: Permission): Boolean {
 *         return state.value.hasPermission(permission)
 *     }
 *     
 *     fun getMissingPermissions(): List<Permission> {
 *         return state.value.getMissingPermissions()
 *     }
 * }
 */

// ========== 优势 ==========

/**
 * 1. 扩展性强：添加新权限只需要在枚举中添加一项
 * 2. 类型安全：使用枚举避免了字符串硬编码
 * 3. 统一管理：所有权限相关逻辑集中在几个类中
 * 4. 易于维护：权限检查和申请逻辑模块化
 * 5. 代码复用：PermissionManager 可以在多个 Activity 中使用
 */